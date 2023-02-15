package fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments

import arrow.core.toOption
import fr.amou.ffbad.tournaments.slack.bot.domain.core.TournamentInfo
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Disciplines
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Query
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Ranking
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Tournaments
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.OnFailure
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.restCall
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments.config.MyFFBadClient
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments.config.RestTournament
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class RestTournaments(
    private val myFFBadClient: MyFFBadClient,
    @Value("\${myffbad.verifyToken}") val verifyToken: String
) : Tournaments {

    private val logger = getLogger(RestTournaments::class.java)

    val myFFBadHeaders = mapOf(
        "Verify-Token" to verifyToken,
        "Caller-URL" to "/api/search/",
        "Content-Type" to "application/json"
    )

    override fun find(query: Query): List<TournamentInfo> {
        return restCall(
            call = myFFBadClient.findTournaments(headers = myFFBadHeaders, query = query.toRestQuery()),
            onSuccess = { response -> response.body()!!.tournaments.map { it.toDomain() } },
            onFailure = OnFailure(emptyList(), "Unable to fetch tournaments.")
        )(logger)
    }
}

fun RestTournament.toDomain() =
    TournamentInfo(
        competitionId = competitionId,
        name = name,
        disciplines = discipline.split(",").map { Disciplines.fromShortName(it) },
        startDate = LocalDate.parse(startDate),
        endDate = LocalDate.parse(endDate),
        joinLimitDate = LocalDate.parse(limitDate),
        location = location,
        sublevels = sublevel.split(",").map { Ranking.valueOf(it.trim()) },
        logo = organizer.logo.toOption().fold({ "" }, { it })
    )


