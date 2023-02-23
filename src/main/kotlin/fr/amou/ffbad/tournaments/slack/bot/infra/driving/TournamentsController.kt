package fr.amou.ffbad.tournaments.slack.bot.infra.driving

import fr.amou.ffbad.tournaments.slack.bot.domain.api.ListTournaments
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class TournamentsController(val listTournaments: ListTournaments) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        listTournaments.from()
    }

}
