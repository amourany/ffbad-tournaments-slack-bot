package fr.amou.ffbad.tournaments.slack.bot.domain.builder

import fr.amou.ffbad.tournaments.slack.bot.domain.model.AgeCategory.SENIOR
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Ranking.*
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentSearchQuery

fun aTournamentSearchQuery(zipCode:String = "92240") = TournamentSearchQuery(
    zipCode = zipCode,
    distance = 10,
    subLevels = listOf(D9, P10, P11, P12, NC),
    categories = listOf(SENIOR),
)
