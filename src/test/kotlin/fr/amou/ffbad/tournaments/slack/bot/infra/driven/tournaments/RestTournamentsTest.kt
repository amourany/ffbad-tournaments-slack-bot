package fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments

import fr.amou.ffbad.tournaments.slack.bot.domain.builder.aTournamentSearchQuery
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.MockCall
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.builder.aRestTournament
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments.config.MyFFBadClient
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments.config.RestTournamentsResponse
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import retrofit2.Response.success
import io.mockk.mockk as mock

class RestTournamentsTest : ShouldSpec({
    isolationMode = InstancePerTest

    val myFFBadClient: MyFFBadClient = mock()

    val restTournaments = RestTournaments(myFFBadClient, "searchVerifyToken", "detailsVerifyToken")

    should("return found tournaments") {

        val tournamentsResponse = RestTournamentsResponse(totalPage = 1, currentPage = 1, tournaments = listOf(aRestTournament()))
        every { myFFBadClient.findTournaments(any(), any()) } returns MockCall(success(tournamentsResponse))

        val foundTournaments = restTournaments.find(aTournamentSearchQuery().toRestQuery())

        foundTournaments shouldHaveSize 1
    }

    should("fetch all tournaments pages") {

        val aRequest = aTournamentSearchQuery().toRestQuery()
        val tournamentsResponsePage1 = RestTournamentsResponse(totalPage = 2, currentPage = 1, tournaments = listOf(aRestTournament("A Page 1 Tournament")))
        val tournamentsResponsePage2 = RestTournamentsResponse(totalPage = 2, currentPage = 2, tournaments = listOf(aRestTournament("A Page 2 Tournament")))

        every { myFFBadClient.findTournaments(any(), aRequest) } returns MockCall(success(tournamentsResponsePage1))
        every { myFFBadClient.findTournaments(any(), aRequest.copy(offset = 20)) } returns MockCall(success(tournamentsResponsePage2))

        val foundTournaments = restTournaments.find(aRequest)

        foundTournaments shouldHaveSize 2
        foundTournaments.map { it.name } shouldBe listOf("A Page 1 Tournament", "A Page 2 Tournament")
    }

    should("return an empty list when there is no tournament") {

        every { myFFBadClient.findTournaments(any(), any()) } returns MockCall(success(RestTournamentsResponse(totalPage = 0, currentPage = 1, tournaments = emptyList())))

        val foundTournaments = restTournaments.find(aTournamentSearchQuery().toRestQuery())

        foundTournaments shouldHaveSize 0
    }
})
