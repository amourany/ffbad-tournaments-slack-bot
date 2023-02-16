package fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.toOption
import fr.amou.ffbad.tournaments.slack.bot.domain.core.TournamentInfo
import fr.amou.ffbad.tournaments.slack.bot.domain.model.*
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Tournaments
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.OnFailure
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.restCall
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments.config.MyFFBadClient
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments.config.RestTournament
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments.config.RestTournamentDetailsResponse
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

    override fun find(query: Query): List<TournamentInfo> {
        val headers =
            mapOf(
                "Caller-URL" to "/api/search/",
                "Content-Type" to "application/json",
                "Verify-Token" to searchVerifyToken
        )

        return restCall(
            call = myFFBadClient.findTournaments(headers = headers, query = query.toRestQuery()),
            onSuccess = { response -> response.body()!!.tournaments.map { it.toDomain() } },
            onFailure = OnFailure(emptyList(), "Unable to fetch tournaments.")
        )(logger)
    }

    override fun details(id: String): Option<TournamentInfoDetails> {
        val headers =
            mapOf(
                "Caller-URL" to "/api/tournament/",
                "currentPersonId" to "1",
                "Content-Type" to "application/json",
                "Verify-Token" to detailsVerifyToken
        )

        return restCall(
            call = myFFBadClient.findTournamentDetails(headers = headers, id = id),
            onSuccess = { response -> Some(response.body()!!.toDomain()) },
            onFailure = OnFailure(None, "Unable to fetch tournaments.")
        )(logger)
    }
}

fun RestTournament.toDomain() =
    TournamentInfo(
        competitionId = number,
        name = name,
        disciplines = discipline.split(",").map { Disciplines.fromShortName(it) },
        startDate = LocalDate.parse(startDate),
        endDate = LocalDate.parse(endDate),
        joinLimitDate = LocalDate.parse(limitDate),
        location = location,
        sublevels = sublevel.split(",").map { Ranking.valueOf(it.trim()) },
        logo = organizer.logo.toOption()
            .fold({ "https://poona.ffbad.org/public/images/federation/logo-instance-4.jpg" }, { it })
    )

fun RestTournamentDetailsResponse.toDomain() =
    TournamentInfoDetails(
        description = description.toOption().fold({ "" }, { it }),
        categories = categories,
        document = documents.map { TournamentDocument(it.type, it.url) }.first { it.type == "Règlement particulier" },
        prices = prices.map { TournamentPrice(it.price, it.registrationTable) }
    )

