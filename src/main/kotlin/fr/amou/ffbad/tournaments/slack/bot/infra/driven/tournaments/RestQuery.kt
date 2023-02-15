package fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments

import fr.amou.ffbad.tournaments.slack.bot.domain.model.Query
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Ranking

data class RestQuery(
    val type: String,
    val text: String,
    val offset:String = "0",
    val postalCode: String,
    val distance: Int,
    val subLevels: List<Int>,
    val categories: List<Int>,
    val dateFrom: String,
    val dateTo: String,
    val sort: String
)


fun Query.toRestQuery() = RestQuery(
    type = type.name,
    text = text,
    postalCode = postalCode,
    distance = distance,
    subLevels = subLevels.map { it.toSubLevel() },
    categories = categories.map { it.ordinal },
    dateFrom = dateFrom.toString(),
    dateTo = dateTo.toString(),
    sort = sort
)

fun Ranking.toSubLevel() = this.ordinal + 115
