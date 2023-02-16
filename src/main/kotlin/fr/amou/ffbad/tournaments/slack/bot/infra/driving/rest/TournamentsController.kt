package fr.amou.ffbad.tournaments.slack.bot.infra.driving.rest

import fr.amou.ffbad.tournaments.slack.bot.domain.api.ListTournaments
import fr.amou.ffbad.tournaments.slack.bot.domain.core.TournamentInfo
import fr.amou.ffbad.tournaments.slack.bot.domain.model.AgeCategory.*
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Query
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Ranking.*
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentType.TOURNAMENT
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime.*

@RestController
@RequestMapping("/api/tournaments")
class TournamentsController(val listTournaments: ListTournaments) {

    @GetMapping("")
    fun listTournaments(): ResponseEntity<List<TournamentInfo>> {
        val query = Query(
            type = TOURNAMENT,
            text = "",
            postalCode = "92320",
            distance = 10,
            subLevels = listOf(D8, D9, P10, P11, P12, NC),
            categories = listOf(SENIOR),
            dateFrom = now(),
            dateTo = now().plusYears(1),
            sort = "dateFrom-ASC"
        )
        listTournaments.from(query)
        return ok().build()
    }
}
