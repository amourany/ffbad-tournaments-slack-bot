package fr.amou.ffbad.tournaments.slack.bot.domain.spi

import fr.amou.ffbad.tournaments.slack.bot.domain.core.TournamentInfo

interface Publication {
    fun publish(info: TournamentInfo)
}
