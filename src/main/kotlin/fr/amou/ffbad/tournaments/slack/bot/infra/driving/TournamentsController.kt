package fr.amou.ffbad.tournaments.slack.bot.infra.driving

import fr.amou.ffbad.tournaments.slack.bot.domain.api.ListTournaments
import fr.amou.ffbad.tournaments.slack.bot.domain.model.AgeCategory.SENIOR
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Query
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Ranking.*
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentType.TOURNAMENT
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.time.LocalDateTime.now

@Component
class TournamentsController(val listTournaments: ListTournaments) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
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
    }
}
