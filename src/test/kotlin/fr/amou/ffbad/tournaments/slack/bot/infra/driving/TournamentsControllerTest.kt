package fr.amou.ffbad.tournaments.slack.bot.infra.driving

import fr.amou.ffbad.tournaments.slack.bot.domain.api.ListTournaments
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class TournamentsControllerTest : ShouldSpec({
    isolationMode = InstancePerTest

    val tournaments: ListTournaments = mockk()

    val tournamentsController = TournamentsController(tournaments)

    should("search for tournaments") {
        // Given
        val jsonQuery = """{"queries":[{"zipCode":"92240", "distance":10, "subLevels":["D9","P10"], "categories":["SENIOR"]}]}"""
        every { tournaments.from(any()) } returns "OK"

        // When
        tournamentsController.run().apply(jsonQuery)

        // Then
        verify(exactly = 1) { tournaments.from(any()) }

    }

    should("search for tournaments with multiple queries") {
        // Given
        val jsonQuery = """{"queries":[{"zipCode":"92240", "distance":10, "subLevels":["D9","P10"], "categories":["SENIOR"]}, {"zipCode":"75015", "distance":10, "subLevels":["D9","P10"], "categories":["SENIOR"]}]}"""
        every { tournaments.from(any()) } returns "OK"

        // When
        tournamentsController.run().apply(jsonQuery)

        // Then
        verify(exactly = 1) { tournaments.from(any()) }

    }

    should("not run search query for an empty zipCode") {
        // Given
        val jsonQuery = """{"queries":[{"zipCode":"", "distance":10, "subLevels":["D9","P10"], "categories":["SENIOR"]}]}"""
        every { tournaments.from(any()) } returns "KO"

        // When
        tournamentsController.run().apply(jsonQuery)

        // Then
        verify(exactly = 0) { tournaments.from(any()) }
    }

    should("not run search query for an unknown ranking") {
        // Given
        val jsonQuery = """{"queries":[{"zipCode":"92240", "distance":10, "subLevels":["ABC","P10"], "categories":["SENIOR"]}]}"""
        every { tournaments.from(any()) } returns "KO"

        // When
        tournamentsController.run().apply(jsonQuery)

        // Then
        verify(exactly = 0) { tournaments.from(any()) }
    }

    should("not run search query for an unknown age category") {
        // Given
        val jsonQuery = """{"queries":[{"zipCode":"92240", "distance":10, "subLevels":["D9","P10"], "categories":["SENIOR", "ABC"]}]}"""
        every { tournaments.from(any()) } returns "KO"

        // When
        tournamentsController.run().apply(jsonQuery)

        // Then
        verify(exactly = 0) { tournaments.from(any()) }
    }

})
