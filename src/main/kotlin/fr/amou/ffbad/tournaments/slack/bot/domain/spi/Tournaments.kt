package fr.amou.ffbad.tournaments.slack.bot.domain.spi

import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentInfo
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentSearchQuery

interface Tournaments {
    fun findAllFrom(query: TournamentSearchQuery): List<TournamentInfo>
}
