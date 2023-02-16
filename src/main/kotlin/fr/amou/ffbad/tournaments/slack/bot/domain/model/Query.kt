package fr.amou.ffbad.tournaments.slack.bot.domain.model

import java.time.LocalDateTime

data class Query(
    val type: TournamentType,
    val text: String,
    val postalCode: String,
    val distance: Int,
    val subLevels: List<Ranking>,
    val categories: List<AgeCategory>,
    val dateFrom: LocalDateTime,
    val dateTo: LocalDateTime,
    val sort: String,
    val offset: Int
)
