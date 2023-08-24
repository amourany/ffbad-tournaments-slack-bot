package fr.amou.ffbad.tournaments.slack.bot.domain.core

import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentInfo
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Cache
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Publication
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Tournaments
import org.slf4j.LoggerFactory.*
import org.springframework.stereotype.Service
import java.time.LocalDate.now

@Service
class TournamentsService(
    private val tournaments: Tournaments, private val publication: Publication,
    private val cache: Cache
) {

    val logger = getLogger(TournamentsService::class.java)

    fun listTournaments() {

        val filteringRules = listOf(
            filterOutAlreadyPublishedTournaments(cache.findAll()),
            filterOutClosedTournaments(),
            filterOutParabadTournaments(),
            filterOutTournamentsFromFFBad(),
            filterOutTournamentsFromCommitteesOtherThanCommittee92()
        )

        val foundTournaments = tournaments.findAll()

        logger.info("Found ${foundTournaments.size} tournaments")

        val filteredTournaments = foundTournaments.filter { tournament -> filteringRules.all { rule -> rule(tournament) } }

        logger.info("Found ${filteredTournaments.size} tournaments after filtering")

        val publishedTournaments = filteredTournaments
            .map {
                val isPublished = publication.publish(it)
                Pair(isPublished, it)
            }
            .filter { (isPublished) -> isPublished }

        logger.info("Published ${publishedTournaments.size} tournaments")

        publishedTournaments.forEach { (_, tournament) -> cache.save(tournament.competitionId, tournament.name) }
    }

    private fun filterOutAlreadyPublishedTournaments(alreadyPublishedTournaments: List<String>): (tournament: TournamentInfo) -> Boolean {
        return { tournament -> !alreadyPublishedTournaments.contains(tournament.competitionId) }
    }

    private fun filterOutClosedTournaments(): (tournament: TournamentInfo) -> Boolean {
        return { tournament -> tournament.joinLimitDate.isAfter(now()) }
    }

    private fun filterOutParabadTournaments():(tournament: TournamentInfo) -> Boolean {
        return { tournament -> !tournament.isParabad }
    }

    private fun filterOutTournamentsFromFFBad(): (tournament: TournamentInfo) -> Boolean {
        return {tournament -> tournament.organizer != "FFBAD" }
    }

    private fun filterOutTournamentsFromCommitteesOtherThanCommittee92(): (tournament: TournamentInfo) -> Boolean {
        return {tournament -> !listOf("CD75", "CD91", "CD93", "CD94").contains(tournament.organizer) }
    }
}
