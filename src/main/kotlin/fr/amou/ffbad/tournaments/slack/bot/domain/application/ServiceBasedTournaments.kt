package fr.amou.ffbad.tournaments.slack.bot.domain.application

import fr.amou.ffbad.tournaments.slack.bot.domain.api.ListTournaments
import fr.amou.ffbad.tournaments.slack.bot.domain.core.TournamentsService
import org.springframework.stereotype.Service

@Service
class ServiceBasedTournaments(val tournamentsService: TournamentsService) : ListTournaments {
    override fun from() = tournamentsService.listTournaments()
}
