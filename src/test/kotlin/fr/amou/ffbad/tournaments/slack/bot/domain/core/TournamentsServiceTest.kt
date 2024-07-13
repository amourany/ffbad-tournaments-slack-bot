package fr.amou.ffbad.tournaments.slack.bot.domain.core

import fr.amou.ffbad.tournaments.slack.bot.domain.builder.aTournament
import fr.amou.ffbad.tournaments.slack.bot.domain.builder.aTournamentSearchQuery
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
        val query = aTournamentSearchQuery()
        val queries = listOf(query)
        every { cache.findAllByQuerySource("test") } returns emptyList()
        every { tournaments.findAllFrom(query) } returns emptyList()

        // When
        tournamentsService.listTournaments(queries, "test")

        // Then
        verify(exactly = 1) { cache.findAllByQuerySource("test") }
        verify(exactly = 1) { tournaments.findAllFrom(query) }
        verify(exactly = 0) { publication.publish(any()) }
        verify(exactly = 0) { cache.save(any(), any(), any()) }
    }

    should("fetch tournaments and tournaments details") {
        // Given
        val query = aTournamentSearchQuery()
        every { cache.findAllByQuerySource("test") } returns emptyList()
        every { tournaments.findAllFrom(query) } returns listOf(
            aTournament(
                competitionId = "12345",
                name = "My tournament"
            )
        )
        every { publication.publish(any()) } returns true
        every { cache.save(any(), any(), any()) } returns Unit

        // When
        tournamentsService.listTournaments(listOf(query), "test")

        // Then
        verify(exactly = 1) { cache.findAllByQuerySource("test") }
        verify(exactly = 1) { tournaments.findAllFrom(query) }
        verify(exactly = 1) { publication.publish(any()) }
        verify(exactly = 1) { cache.save("12345", "My tournament","test") }
    }

    should("fetch tournaments and tournaments details for multiple queries") {
        // Given
        val query92 = aTournamentSearchQuery(zipCode = "92240")
        val query75 = aTournamentSearchQuery(zipCode = "75015")
        every { cache.findAllByQuerySource("test") } returns emptyList()
        every { tournaments.findAllFrom(query92) } returns listOf(
            aTournament(
                competitionId = "12345",
                name = "My tournament"
            )
        )
        every { tournaments.findAllFrom(query75) } returns listOf(
            aTournament(
                competitionId = "6789",
                name = "My tournament in Paris"
            )
        )
        every { publication.publish(any()) } returns true
        every { cache.save(any(), any(), any()) } returns Unit

        // When
        tournamentsService.listTournaments(listOf(query92, query75), "test")

        // Then
        verify(exactly = 1) { cache.findAllByQuerySource("test") }
        verify(exactly = 1) { tournaments.findAllFrom(query92) }
        verify(exactly = 1) { tournaments.findAllFrom(query75) }
        verify(exactly = 2) { publication.publish(any()) }
        verify(exactly = 1) { cache.save("12345", "My tournament", "test") }
        verify(exactly = 1) { cache.save("6789", "My tournament in Paris", "test") }
    }

    should("fetch tournaments and tournaments details for multiple queries with deduplication") {
        // Given
        val query92 = aTournamentSearchQuery(zipCode = "92240")
        val query75 = aTournamentSearchQuery(zipCode = "75015")
        every { cache.findAllByQuerySource("test") } returns emptyList()
        every { tournaments.findAllFrom(query92) } returns listOf(aTournament(competitionId = "12345", name = "My tournament"))
        every { tournaments.findAllFrom(query75) } returns listOf(
            aTournament(competitionId = "12345", name = "My tournament"),
            aTournament(competitionId = "6789", name = "My tournament in Paris")
        )
        every { publication.publish(any()) } returns true
        every { cache.save(any(), any(), any()) } returns Unit

        // When
        tournamentsService.listTournaments(listOf(query92, query75), "test")

        // Then
        verify(exactly = 1) { cache.findAllByQuerySource("test") }
        verify(exactly = 1) { tournaments.findAllFrom(query92) }
        verify(exactly = 1) { tournaments.findAllFrom(query75) }
        verify(exactly = 2) { publication.publish(any()) }
        verify(exactly = 1) { cache.save("12345", "My tournament", "test") }
        verify(exactly = 1) { cache.save("6789", "My tournament in Paris", "test") }
    }

    should("filter out tournaments closed for registration") {
        // Given
        val query = aTournamentSearchQuery()
        every { cache.findAllByQuerySource("test") } returns emptyList()
        every { tournaments.findAllFrom(query) } returns listOf(aTournament(joinLimitDate = now().minusDays(1)))

        // When
        tournamentsService.listTournaments(listOf(query), "test")

        // Then
        verify(exactly = 1) { cache.findAllByQuerySource("test") }
        verify(exactly = 1) { tournaments.findAllFrom(query) }
        verify(exactly = 0) { publication.publish(any()) }
    }

    should("filter out parabad tournaments") {
        // Given
        val query = aTournamentSearchQuery()
        every { cache.findAllByQuerySource("test") } returns emptyList()
        every { tournaments.findAllFrom(query) } returns listOf(aTournament(isParabad = true))

        // When
        tournamentsService.listTournaments(listOf(query), "test")

        // Then
        verify(exactly = 1) { tournaments.findAllFrom(query) }
        verify(exactly = 0) { publication.publish(any()) }
    }

    should("not republished an already published tournament") {
        // Given
        val query = aTournamentSearchQuery()
        every { cache.findAllByQuerySource("test") } returns listOf("1")
        every { tournaments.findAllFrom(query) } returns listOf(aTournament(competitionId = "1"))

        // When
        tournamentsService.listTournaments(listOf(query), "test")

        // Then
        verify(exactly = 1) { tournaments.findAllFrom(query) }
        verify(exactly = 0) { publication.publish(any()) }
        verify(exactly = 0) { cache.save(any(), any(), any()) }
    }

    should("not save when publication fails") {
        // Given
        val query = aTournamentSearchQuery()
        every { cache.findAllByQuerySource("test") } returns emptyList()
        every { tournaments.findAllFrom(query) } returns listOf(aTournament(competitionId = "1"))
        every { publication.publish(any()) } returns false

        // When
        tournamentsService.listTournaments(listOf(query), "test")

        // Then
        verify(exactly = 1) { tournaments.findAllFrom(query) }
        verify(exactly = 1) { publication.publish(any()) }
        verify(exactly = 0) { cache.save(any(), any(), any()) }
    }

    should("filter out tournaments from FFBAD") {
        // Given
        val query = aTournamentSearchQuery()
        every { cache.findAllByQuerySource("test") } returns emptyList()
        every { tournaments.findAllFrom(query) } returns listOf(aTournament(organizer = "FFBAD"))

        // When
        tournamentsService.listTournaments(listOf(query), "test")

        // Then
        verify(exactly = 1) { tournaments.findAllFrom(query) }
        verify(exactly = 0) { publication.publish(any()) }
    }

    should("not filter out tournaments from clubs") {
        // Given
        val query = aTournamentSearchQuery()
        every { cache.findAllByQuerySource("test") } returns emptyList()
        every { tournaments.findAllFrom(query) } returns listOf(
            aTournament(
                competitionId = "12345",
                name = "My tournament",
                organizer = "USMM92"
            )
        )
        every { publication.publish(any()) } returns true
        every { cache.save(any(), any(), any()) } returns Unit

        // When
        tournamentsService.listTournaments(listOf(query), "test")

        // Then
        verify(exactly = 1) { cache.findAllByQuerySource("test") }
        verify(exactly = 1) { tournaments.findAllFrom(query) }
        verify(exactly = 1) { publication.publish(any()) }
        verify(exactly = 1) { cache.save("12345", "My tournament", "test") }
    }

    should("not filter out tournaments from committee associated with the club") {
        // Given
        val query = aTournamentSearchQuery()
        every { cache.findAllByQuerySource("test") } returns emptyList()
        every { tournaments.findAllFrom(query) } returns listOf(
            aTournament(
                competitionId = "12345",
                name = "My tournament",
                organizer = "CD92"
            )
        )
        every { publication.publish(any()) } returns true
        every { cache.save(any(), any(), any()) } returns Unit

        // When
        tournamentsService.listTournaments(listOf(query), "test")

        // Then
        verify(exactly = 1) { cache.findAllByQuerySource("test") }
        verify(exactly = 1) { tournaments.findAllFrom(query) }
        verify(exactly = 1) { publication.publish(any()) }
        verify(exactly = 1) { cache.save("12345", "My tournament", "test") }
    }

    should("not filter out tournaments from committees associated with the multiple clubs") {
        // Given
        val queryFrom92 = aTournamentSearchQuery(zipCode = "92240")
        val queryFrom75 = aTournamentSearchQuery(zipCode = "75015")
        every { cache.findAllByQuerySource("test") } returns emptyList()
        every { tournaments.findAllFrom(queryFrom92) } returns listOf(
            aTournament(
                competitionId = "12345",
                name = "My tournament",
                organizer = "CD92"
            )
        )
        every { tournaments.findAllFrom(queryFrom75) } returns listOf(
            aTournament(
                competitionId = "6789",
                name = "My other tournament",
                organizer = "CD75"
            )
        )
        every { publication.publish(any()) } returns true
        every { cache.save(any(), any(), any()) } returns Unit

        // When
        tournamentsService.listTournaments(listOf(queryFrom92, queryFrom75), "test")

        // Then
        verify(exactly = 1) { cache.findAllByQuerySource("test") }
        verify(exactly = 1) { tournaments.findAllFrom(queryFrom92) }
        verify(exactly = 1) { tournaments.findAllFrom(queryFrom75) }
        verify(exactly = 2) { publication.publish(any()) }
        verify(exactly = 1) { cache.save("12345", "My tournament", "test") }
        verify(exactly = 1) { cache.save("6789", "My other tournament", "test") }
    }

    should("filter out tournaments from other committees than the club one") {
        // Given
        val query = aTournamentSearchQuery()
        every { cache.findAllByQuerySource("test") } returns emptyList()
        every { tournaments.findAllFrom(query) } returns listOf(aTournament(organizer = "CD75"))

        // When
        tournamentsService.listTournaments(listOf(query), "test")

        // Then
        verify(exactly = 1) { tournaments.findAllFrom(query) }
        verify(exactly = 0) { publication.publish(any()) }
    }
})

