package fr.amou.ffbad.tournaments.slack.bot.domain.spi

import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentInfo

interface Publication {
    fun publish(info: TournamentInfo): Boolean
    fun publishError(stackTrace: String)
}
