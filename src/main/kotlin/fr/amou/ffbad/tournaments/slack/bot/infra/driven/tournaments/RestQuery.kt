package fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments

import fr.amou.ffbad.tournaments.slack.bot.domain.model.Ranking
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentSearchQuery
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentType
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentType.TOURNAMENT
import java.time.LocalDateTime.now

data class RestQuery(
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

fun TournamentSearchQuery.toRestQuery() = RestQuery(
    type = TOURNAMENT,
    text = "",
    postalCode = zipCode,
    distance = distance,
    sublevels = subLevels.map { it.toSubLevel() },
    categories = categories.map { it.ordinal },
    dateFrom = now().toString(),
    dateTo = now().plusYears(1).toString(),
    sort = "dateFrom-ASC",
    offset = 0
)

fun Ranking.toSubLevel() = this.ordinal + 115
