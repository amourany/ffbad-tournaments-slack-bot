package fr.amou.ffbad.tournaments.slack.bot.domain.api

import fr.amou.ffbad.tournaments.slack.bot.domain.core.TournamentInfo
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Query

interface ListTournaments {
    fun from(query: Query): List<TournamentInfo>
}
