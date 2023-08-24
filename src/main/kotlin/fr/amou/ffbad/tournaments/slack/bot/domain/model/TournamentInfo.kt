package fr.amou.ffbad.tournaments.slack.bot.domain.model

import java.time.LocalDate

data class TournamentInfo(
    val competitionId: String,
    val name: String,
    val disciplines: List<Disciplines>,
    val distance: Int,
    val dates: List<LocalDate>,
    val joinLimitDate: LocalDate,
    val location: String,
    val sublevels: List<Ranking>,
    val logo: String,
    val categories: List<String>,
    val description: String,
    val document: TournamentDocument,
    val isParabad: Boolean,
    val prices: List<TournamentPrice>,
    val organizer: String
)

data class TournamentDocument(
    val type: String,
    val url: String
)

data class TournamentPrice(
    val price: Int,
    val registrationTable: Int
)
