package fr.amou.ffbad.tournaments.slack.bot.domain.spi

import arrow.core.Option
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentInfo
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Query
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentInfoDetails

interface Tournaments {
    fun find(query:Query): List<TournamentInfo>
    fun details(id: String): Option<TournamentInfoDetails>
}
