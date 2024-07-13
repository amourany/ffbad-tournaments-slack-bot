package fr.amou.ffbad.tournaments.slack.bot.domain.application

import fr.amou.ffbad.tournaments.slack.bot.domain.builder.aTournamentSearchQuery
import fr.amou.ffbad.tournaments.slack.bot.domain.core.TournamentsService
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import io.mockk.verify
import io.mockk.mockk as mock

class ServiceBasedTournamentsTest: ShouldSpec({
    isolationMode = InstancePerTest

    val tournamentsService:TournamentsService = mock()

    val serviceBasedTournaments = ServiceBasedTournaments(tournamentsService)
    should("fetch tournaments") {

        every { tournamentsService.listTournaments(any()) } returns Unit

        serviceBasedTournaments.from(listOf(aTournamentSearchQuery()))

        verify(exactly = 1) { tournamentsService.listTournaments(any()) }
        verify(exactly = 0) { tournamentsService.publishError(any()) }
    }

    should("publish message when an error occurs when fetching tournaments") {

        every { tournamentsService.listTournaments(any()) } throws NoSuchElementException()
        every { tournamentsService.publishError(any()) } returns Unit

        serviceBasedTournaments.from(listOf(aTournamentSearchQuery()))

        verify(exactly = 1) { tournamentsService.listTournaments(any()) }
        verify(exactly = 1) { tournamentsService.publishError(any()) }
    }
})
