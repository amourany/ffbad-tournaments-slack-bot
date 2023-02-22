package fr.amou.ffbad.tournaments.slack.bot.domain.spi

import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentInfo
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentInfoDetails

interface Publication {
    fun publish(info: TournamentInfo, details: TournamentInfoDetails): Boolean
}
