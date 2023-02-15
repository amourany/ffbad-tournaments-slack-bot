package fr.amou.ffbad.tournaments.slack.bot.domain.spi

import fr.amou.ffbad.tournaments.slack.bot.domain.core.TournamentInfo
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Query

interface Tournaments {
    fun find(query:Query): List<TournamentInfo>
}
