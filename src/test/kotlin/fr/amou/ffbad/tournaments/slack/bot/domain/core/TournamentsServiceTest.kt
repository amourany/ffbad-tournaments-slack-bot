package fr.amou.ffbad.tournaments.slack.bot.domain.core

import fr.amou.ffbad.tournaments.slack.bot.domain.builder.aTournament
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Cache
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
    val cache: Cache = mock()

    val tournamentsService = TournamentsService(tournaments, publication, cache)

    should("not attempt to fetch tournament details when 0 tournaments are found") {
        // Given
        every { cache.findAll() } returns emptyList()
        every { tournaments.findAll() } returns emptyList()

        // When
        tournamentsService.listTournaments()

        // Then
        verify(exactly = 1) { cache.findAll() }
        verify(exactly = 1) { tournaments.findAll() }
        verify(exactly = 0) { publication.publish(any()) }
        verify(exactly = 0) { cache.save(any(), any()) }
    }

    should("fetch tournaments and tournaments details") {
        // Given
        every { cache.findAll() } returns emptyList()
        every { tournaments.findAll() } returns listOf(aTournament(competitionId = "12345", name = "My tournament"))
        every { publication.publish(any()) } returns true
        every { cache.save(any(), any()) } returns Unit

        // When
        tournamentsService.listTournaments()

        // Then
        verify(exactly = 1) { cache.findAll() }
        verify(exactly = 1) { tournaments.findAll() }
        verify(exactly = 1) { publication.publish(any()) }
        verify(exactly = 1) { cache.save("12345", "My tournament") }
    }

    should("filter out tournaments closed for registration") {
        // Given
        every { cache.findAll() } returns emptyList()
        every { tournaments.findAll() } returns listOf(aTournament(joinLimitDate = now().minusDays(1)))

        // When
        tournamentsService.listTournaments()

        // Then
        verify(exactly = 1) { cache.findAll() }
        verify(exactly = 1) { tournaments.findAll() }
        verify(exactly = 0) { publication.publish(any()) }
    }

    should("filter out parabad tournaments") {
        // Given
        every { cache.findAll() } returns emptyList()
        every { tournaments.findAll() } returns listOf(aTournament(isParabad = true))

        // When
        tournamentsService.listTournaments()

        // Then
        verify(exactly = 1) { tournaments.findAll() }
        verify(exactly = 0) { publication.publish(any()) }
    }

    should("not republished an already published tournament") {
        // Given
        every { cache.findAll() } returns listOf("1")
        every { tournaments.findAll() } returns listOf(aTournament(competitionId = "1"))

        // When
        tournamentsService.listTournaments()

        // Then
        verify(exactly = 1) { tournaments.findAll() }
        verify(exactly = 0) { publication.publish(any()) }
        verify(exactly = 0) { cache.save(any(), any()) }
    }

    should("not save when publication fails") {
        // Given
        every { cache.findAll() } returns emptyList()
        every { tournaments.findAll() } returns listOf(aTournament(competitionId = "1"))
        every { publication.publish(any()) } returns false

        // When
        tournamentsService.listTournaments()

        // Then
        verify(exactly = 1) { tournaments.findAll() }
        verify(exactly = 1) { publication.publish(any()) }
        verify(exactly = 0) { cache.save(any(), any()) }
    }
})
