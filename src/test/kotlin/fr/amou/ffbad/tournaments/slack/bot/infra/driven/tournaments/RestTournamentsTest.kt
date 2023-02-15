package fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments

import fr.amou.ffbad.tournaments.slack.bot.IntegrationSpec
import fr.amou.ffbad.tournaments.slack.bot.domain.model.AgeCategory.SENIOR
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Query
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Ranking.*
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentType.TOURNAMENT
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments.config.MyFFBadClient
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class RestTournamentsTest(private val myFFBadClient: MyFFBadClient): IntegrationSpec({

    val tournaments = RestTournaments(myFFBadClient, verifyToken = "")

    should("fetch tournaments from MyFFBad Client") {
        // Given
        val query = Query(
            type = TOURNAMENT,
            text = "",
            postalCode = "92320",
            distance = 10,
            subLevels = listOf(P10, P11, P12),
            categories = listOf(SENIOR),
            dateFrom = LocalDateTime.now(),
            dateTo = LocalDateTime.now(),
            sort = "ASC"
        )

        // When
        val result = tournaments.find(query)

        // Then
        result.size shouldBe 15
    }
})
