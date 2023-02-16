package fr.amou.ffbad.tournaments.slack.bot.domain.core

import arrow.core.Some
import fr.amou.ffbad.tournaments.slack.bot.domain.builder.aTournament
import fr.amou.ffbad.tournaments.slack.bot.domain.builder.aTournamentDetails
import fr.amou.ffbad.tournaments.slack.bot.domain.model.AgeCategory.SENIOR
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Query
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Ranking.*
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentType.TOURNAMENT
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Publication
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Tournaments
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import io.mockk.verify
import java.time.LocalDateTime.now
import io.mockk.mockk as mock

class TournamentsServiceTest : ShouldSpec({
    isolationMode = InstancePerTest

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
        tournamentsService.listTournaments(query)

        // Then
        verify(exactly = 1) {tournaments.find(any())}
        verify(exactly = 0) {tournaments.details(any())}
        verify(exactly = 0) {publication.publish(any(), any())}
    }

    should("fetch tournaments and tournaments details") {
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
        every { tournaments.details(any()) } returns Some(aTournamentDetails())
        every { publication.publish(any(), any()) } returns Unit

        // When
        tournamentsService.listTournaments(query)

        // Then
        verify(exactly = 1) {tournaments.find(any())}
        verify(exactly = 1) {tournaments.details(any())}
        verify(exactly = 1) {publication.publish(any(), any())}
    }
})
