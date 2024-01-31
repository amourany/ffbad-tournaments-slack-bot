package fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments

import arrow.core.*
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentInfo
import fr.amou.ffbad.tournaments.slack.bot.domain.model.*
import fr.amou.ffbad.tournaments.slack.bot.domain.model.AllowedDocumentsType.Companion.findFromValue
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Tournaments
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.OnFailure
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.restCall
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments.config.MyFFBadClient
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments.config.RestTournament
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments.config.RestTournamentDetailsResponse
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class RestTournaments(
    private val myFFBadClient: MyFFBadClient,
    @Value("\${myffbad.searchVerifyToken}") val searchVerifyToken: String,
    @Value("\${myffbad.detailsVerifyToken}") val detailsVerifyToken: String
) : Tournaments {

    private val logger = getLogger(RestTournaments::class.java)

    override fun findAllFrom(query: TournamentSearchQuery): List<TournamentInfo> {

        logger.info("Fetching all tournaments")

        val tournaments = find(query.toRestQuery())

        return runBlocking(Dispatchers.Default) {
            tournaments.parallelMap { tournament ->
                details(tournament.number).fold({ None }, { Some(toDomain(tournament, it)) })
            }.filterOption()
        }
    }

    fun find(query: RestQuery): List<RestTournament> {
        val headers =
            mapOf(
                "Caller-URL" to "/api/search/",
                "Content-Type" to "application/json",
                "Verify-Token" to searchVerifyToken
            )

        return restCall(
            call = myFFBadClient.findTournaments(headers = headers, query = query),
            onSuccess = { response ->
                val body = response.body()!!
                when (body.currentPage) {
                    body.totalPage -> body.tournaments
                    else -> body.tournaments.plus(find(query.copy(offset = body.currentPage * 20)))
                }
            },
            onFailure = OnFailure(emptyList(), "Unable to fetch tournaments.")
        )(logger)
    }

    fun details(id: String): Option<RestTournamentDetailsResponse> {
        val headers =
            mapOf(
                "Caller-URL" to "/api/tournament/",
                "currentPersonId" to "1",
                "Content-Type" to "application/json",
                "Verify-Token" to detailsVerifyToken
            )

        return restCall(
            call = myFFBadClient.findTournamentDetails(headers = headers, id = id),
            onSuccess = { response -> Some(response.body()!!) },
            onFailure = OnFailure(None, "Unable to fetch tournaments.")
        )(logger)
    }

    fun toDomain(tournament: RestTournament, details: RestTournamentDetailsResponse): TournamentInfo {
        val allowedDocumentsTypes = AllowedDocumentsType.entries.map { it.value }
        return TournamentInfo(
            competitionId = tournament.number,
            name = tournament.name,
            disciplines = tournament.discipline.split(",").mapNotNull { Disciplines.fromShortName(it) },
            distance = tournament.distance.substringBefore(".").toInt(),
            dates = LocalDate.parse(tournament.startDate).datesUntil(LocalDate.parse(tournament.endDate).plusDays(1))
                .toList(),
            joinLimitDate = LocalDate.parse(tournament.limitDate),
            location = tournament.location,
            sublevels = tournament.sublevel.split(",").map { Ranking.valueOf(it.trim()) },
            logo = tournament.organizer.logo.toOption()
                .fold({ "https://poona.ffbad.org/public/images/federation/logo-instance-4.jpg" }, { it }),
            categories = details.categories,
            description = details.description.toOption().fold({ "" }, { it }),
            documents = details.documents
                .filter { allowedDocumentsTypes.contains(it.type) }
                .map { TournamentDocument(findFromValue(it.type), it.url) },
            isParabad = details.isParabad,
            prices = details.prices.map { TournamentPrice(it.price, it.registrationTable) },
            organizer = tournament.organizer.initials
        )
    }
}

suspend fun <A, B> Iterable<A>.parallelMap(f: suspend (A) -> B): List<B> =
    coroutineScope { map { async { f(it) } }.awaitAll() }
