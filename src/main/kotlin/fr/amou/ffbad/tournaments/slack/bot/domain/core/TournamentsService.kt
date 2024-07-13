package fr.amou.ffbad.tournaments.slack.bot.domain.core

import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentInfo
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentSearchQuery
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Cache
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Publication
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Tournaments
import org.slf4j.LoggerFactory.*
import org.springframework.stereotype.Service
import java.time.LocalDate.now

val DEPARTMENTAL_COMMITTEE_REGEX = """CD[0-9]*""".toRegex()

@Service
class TournamentsService(
    private val tournaments: Tournaments,
    private val publication: Publication,
    private val cache: Cache
) {

    val logger = getLogger(TournamentsService::class.java)

    fun listTournaments(queries: List<TournamentSearchQuery>, querySource: String) {

        val queriedCommittees = queries.map { it.zipCode }

        val filteringRules = listOf(
            filterOutAlreadyPublishedTournaments(cache.findAllByQuerySource(querySource)),
            filterOutClosedTournaments(),
            filterOutParabadTournaments(),
            filterOutTournamentsFromFFBad(),
            filterOutTournamentsFromCommitteesOtherThanCurrentClubCommittee(queriedCommittees)
        )

        logger.info("Fetching all tournaments")

        val foundTournaments = queries.mapIndexed { index, query ->
            tournaments.findAllFrom(query)
                .also { logger.info("Found ${it.size} tournaments from query $index") }
        }.flatten()
            .toSet()

        val (filteredInTournaments, filteredOutTournaments) = foundTournaments.partition { tournament ->
            filteringRules.all { rule -> rule(tournament) }
        }

        filteredOutTournaments.forEach { tournament ->
            logger.info("Filtered out tournament : (name=${tournament.name}, joinLimitDate=${tournament.joinLimitDate}, isParabad=${tournament.isParabad}, organizer=${tournament.organizer})")
        }

        logger.info("Found ${filteredInTournaments.size} tournaments after filtering")

        val publishedTournaments = filteredInTournaments
            .map {
                val isPublished = publication.publish(it)
                Pair(isPublished, it)
            }
            .filter { (isPublished) -> isPublished }
            .also { logger.info("Published ${it.size} tournaments") }

        publishedTournaments.forEach { (_, tournament) -> cache.save(tournament.competitionId, tournament.name, querySource) }
    }

    fun publishError(throwable: Throwable) {
        logger.error(throwable.message, throwable)
        publication.publishError(throwable.stackTraceToString())
    }

    private fun filterOutAlreadyPublishedTournaments(alreadyPublishedTournaments: List<String>): (tournament: TournamentInfo) -> Boolean {
        return { tournament -> !alreadyPublishedTournaments.contains(tournament.competitionId) }
    }

    private fun filterOutClosedTournaments(): (tournament: TournamentInfo) -> Boolean {
        return { tournament -> tournament.joinLimitDate.isAfter(now()) }
    }

    private fun filterOutParabadTournaments(): (tournament: TournamentInfo) -> Boolean {
        return { tournament -> !tournament.isParabad }
    }

    private fun filterOutTournamentsFromFFBad(): (tournament: TournamentInfo) -> Boolean {
        return { tournament -> tournament.organizer != "FFBAD" }
    }

    private fun filterOutTournamentsFromCommitteesOtherThanCurrentClubCommittee(queryZipCode: List<String>): (tournament: TournamentInfo) -> Boolean {
        val clubDepartmentShortName = queryZipCode.map { it.substring(0, 2) }.map { "CD$it" }
        return { tournament -> !DEPARTMENTAL_COMMITTEE_REGEX.matches(tournament.organizer) || clubDepartmentShortName.contains(tournament.organizer) }
    }
}
