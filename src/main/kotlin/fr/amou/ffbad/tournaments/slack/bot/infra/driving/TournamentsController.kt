package fr.amou.ffbad.tournaments.slack.bot.infra.driving

import fr.amou.ffbad.tournaments.slack.bot.domain.api.ListTournaments
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.function.Function


@Component
class TournamentsController(val listTournaments: ListTournaments) {

    @Bean
    fun run(): Function<String, String> {
        return Function<String, String> {
            listTournaments.from()
            "OK"
        }
    }
}
