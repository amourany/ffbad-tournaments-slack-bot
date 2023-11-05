package fr.amou.ffbad.tournaments.slack.bot.domain.builder

import fr.amou.ffbad.tournaments.slack.bot.domain.model.AgeCategory
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Ranking
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentSearchQuery

fun aTournamentSearchQuery() = TournamentSearchQuery(
    zipCode = "92240",
    distance = 10,
    subLevels = listOf(Ranking.D9, Ranking.P10, Ranking.P11, Ranking.P12, Ranking.NC),
    categories = listOf(AgeCategory.SENIOR),
)
