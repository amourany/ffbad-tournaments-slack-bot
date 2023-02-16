package fr.amou.ffbad.tournaments.slack.bot.domain.builder

import fr.amou.ffbad.tournaments.slack.bot.domain.model.AgeCategory
import fr.amou.ffbad.tournaments.slack.bot.domain.model.AgeCategory.SENIOR
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Query
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Ranking
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Ranking.*
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentType
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentType.TOURNAMENT
import java.time.LocalDateTime
import java.time.LocalDateTime.of

fun aQuery(
    type: TournamentType = TOURNAMENT,
    text: String = "",
    postalCode: String = "92320",
    distance: Int = 10,
    subLevels: List<Ranking> = listOf(P10, P11, P12),
    categories: List<AgeCategory> = listOf(SENIOR),
    dateFrom: LocalDateTime = of(2023, 2, 16, 12, 0, 0),
    dateTo: LocalDateTime = of(2024, 2, 16, 12, 0, 0),
    sort: String = "ASC"
) =
    Query(
        type = type,
        text = text,
        postalCode = postalCode,
        distance = distance,
        subLevels = subLevels,
        categories = categories,
        dateFrom = dateFrom,
        dateTo = dateTo,
        sort = sort
    )
