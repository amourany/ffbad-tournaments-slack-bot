package fr.amou.ffbad.tournaments.slack.bot.domain.core

import arrow.core.Some
import fr.amou.ffbad.tournaments.slack.bot.domain.builder.aQuery
import fr.amou.ffbad.tournaments.slack.bot.domain.builder.aTournament
import fr.amou.ffbad.tournaments.slack.bot.domain.builder.aTournamentDetails
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Publication
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Tournaments
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import io.mockk.verify
import java.time.LocalDate.now
import io.mockk.mockk as mock

class TournamentsServiceTest : ShouldSpec({
    isolationMode = InstancePerTest

    val tournaments: Tournaments = mock()
    val publication: Publication = mock()

    val tournamentsService = TournamentsService(tournaments, publication)

    should("not attempt to fetch tournament details when 0 tournaments are found") {
        // Given
        val query = aQuery()
        every { tournaments.find(any()) } returns emptyList()

        // When
        tournamentsService.listTournaments(query)

        // Then
        verify(exactly = 1) { tournaments.find(any()) }
        verify(exactly = 0) { tournaments.details(any()) }
        verify(exactly = 0) { publication.publish(any(), any()) }
    }

    should("fetch tournaments and tournaments details") {
        // Given
        val query = aQuery()
        every { tournaments.find(any()) } returns listOf(aTournament(name = "My tournament"))
        every { tournaments.details(any()) } returns Some(aTournamentDetails())
        every { publication.publish(any(), any()) } returns Unit

        // When
        tournamentsService.listTournaments(query)

        // Then
        verify(exactly = 1) { tournaments.find(any()) }
        verify(exactly = 1) { tournaments.details(any()) }
        verify(exactly = 1) { publication.publish(any(), any()) }
    }

    should("filter out tournaments closed for registration") {
        // Given
        val query = aQuery()
        every { tournaments.find(any()) } returns listOf(aTournament(joinLimitDate = now().minusDays(1)))
        every { tournaments.details(any()) } returns Some(aTournamentDetails())

        // When
        tournamentsService.listTournaments(query)

        // Then
        verify(exactly = 1) { tournaments.find(any()) }
        verify(exactly = 1) { tournaments.details(any()) }
        verify(exactly = 0) { publication.publish(any(), any()) }
    }

    should("filter out parabad tournaments") {
        // Given
        val query = aQuery()
        every { tournaments.find(any()) } returns listOf(aTournament(joinLimitDate = now().plusDays(1)))
        every { tournaments.details(any()) } returns Some(aTournamentDetails(isParabad = true))

        // When
        tournamentsService.listTournaments(query)

        // Then
        verify(exactly = 1) { tournaments.find(any()) }
        verify(exactly = 1) { tournaments.details(any()) }
        verify(exactly = 0) { publication.publish(any(), any()) }
    }
})
