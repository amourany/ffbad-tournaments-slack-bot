package fr.amou.ffbad.tournaments.slack.bot.domain.application

import fr.amou.ffbad.tournaments.slack.bot.domain.api.ListTournaments
import fr.amou.ffbad.tournaments.slack.bot.domain.core.TournamentsService
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentSearchQuery
import org.springframework.stereotype.Service

@Service
class ServiceBasedTournaments(val tournamentsService: TournamentsService) : ListTournaments {
    override fun from(query: TournamentSearchQuery) = runCatching { tournamentsService.listTournaments(query)}
        .fold(
            onSuccess = {},
            onFailure = { tournamentsService.publishError(it) }
        )
}
