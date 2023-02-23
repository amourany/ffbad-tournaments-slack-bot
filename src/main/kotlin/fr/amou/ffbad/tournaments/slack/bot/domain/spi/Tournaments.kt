package fr.amou.ffbad.tournaments.slack.bot.domain.spi

import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentInfo

interface Tournaments {
    fun findAll(): List<TournamentInfo>
}
