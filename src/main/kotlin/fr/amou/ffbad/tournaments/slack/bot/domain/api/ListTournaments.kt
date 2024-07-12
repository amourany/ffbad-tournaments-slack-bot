package fr.amou.ffbad.tournaments.slack.bot.domain.api

import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentSearchQuery

interface ListTournaments {
    fun from(query: TournamentSearchQuery): String
}
