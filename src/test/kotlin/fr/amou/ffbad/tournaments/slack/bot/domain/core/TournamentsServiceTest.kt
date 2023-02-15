package fr.amou.ffbad.tournaments.slack.bot.domain.core

import fr.amou.ffbad.tournaments.slack.bot.domain.builder.aTournament
import fr.amou.ffbad.tournaments.slack.bot.domain.model.AgeCategory.SENIOR
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Query
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Ranking.*
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentType.TOURNAMENT
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Publication
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Tournaments
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import java.time.LocalDateTime.now
import io.mockk.mockk as mock

class TournamentsServiceTest : ShouldSpec({

    val tournaments: Tournaments = mock()
    val publication: Publication = mock()

    val tournamentsService = TournamentsService(tournaments, publication)

    should("return an empty list when no tournaments are found") {
        // Given
        val query = Query(
            type = TOURNAMENT,
            text = "",
            postalCode = "92320",
            distance = 10,
            subLevels = listOf(P10, P11, P12),
            categories = listOf(SENIOR),
            dateFrom = now(),
            dateTo = now(),
            sort = "ASC"
        )
        every { tournaments.find(any()) } returns emptyList()

        // When
        val foundTournaments = tournamentsService.listTournaments(query)

        // Then
        foundTournaments shouldBe emptyList()
    }

    should("fetch tournaments") {
        // Given
        val query = Query(
            type = TOURNAMENT,
            text = "",
            postalCode = "92320",
            distance = 10,
            subLevels = listOf(P10, P11, P12),
            categories = listOf(SENIOR),
            dateFrom = now(),
            dateTo = now(),
            sort = "ASC"
        )
        every { tournaments.find(any()) } returns listOf(aTournament(name="My tournament"))
        every { publication.publish(any()) } returns Unit

        // When
        val foundTournaments = tournamentsService.listTournaments(query)

        // Then
        foundTournaments shouldBe listOf(aTournament(name="My tournament"))
    }
})
