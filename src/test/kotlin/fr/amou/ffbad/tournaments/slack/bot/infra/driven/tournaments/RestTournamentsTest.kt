package fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments

import fr.amou.ffbad.tournaments.slack.bot.IntegrationSpec
import fr.amou.ffbad.tournaments.slack.bot.domain.builder.aQuery
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments.config.MyFFBadClient
import io.kotest.matchers.shouldBe

class RestTournamentsTest(private val myFFBadClient: MyFFBadClient) : IntegrationSpec({

    val tournaments = RestTournaments(myFFBadClient, searchVerifyToken = "", detailsVerifyToken = "")

    should("fetch tournaments from MyFFBad Client") {
        // Given
        val query = aQuery()

        // When
        val result = tournaments.find(query)

        // Then
        result.size shouldBe 15
    }
})
