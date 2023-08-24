package fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments

import fr.amou.ffbad.tournaments.slack.bot.domain.model.AgeCategory.SENIOR
import fr.amou.ffbad.tournaments.slack.bot.domain.model.AgeCategory.VETERAN
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Ranking
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Ranking.*
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentType
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentType.TOURNAMENT
import java.time.LocalDateTime.now

data class Query(
    val type: TournamentType,
    val text: String,
    val offset: Int,
    val postalCode: String,
    val distance: Int,
    val sublevels: List<Int>,
    val categories: List<Int>,
    val dateFrom: String,
    val dateTo: String,
    val sort: String
)

fun aQuery(
    offset: Int = 0
) = Query(
    type = TOURNAMENT,
    text = "",
    postalCode = "92240",
    distance = 12,
    sublevels = listOf(D8, D9, P10, P11, P12, NC).map { it.toSubLevel() },
    categories = listOf(SENIOR.ordinal, VETERAN.ordinal),
    dateFrom = now().toString(),
    dateTo = now().plusYears(1).toString(),
    sort = "dateFrom-ASC",
    offset = offset
)

fun Ranking.toSubLevel() = this.ordinal + 115
