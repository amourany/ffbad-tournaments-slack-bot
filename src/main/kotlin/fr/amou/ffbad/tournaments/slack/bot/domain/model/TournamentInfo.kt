package fr.amou.ffbad.tournaments.slack.bot.domain.model

import java.time.LocalDate

data class TournamentInfo(
    val competitionId: String,
    val name: String,
    val disciplines: List<Disciplines>,
    val dates: List<LocalDate>,
    val joinLimitDate: LocalDate,
    val location: String,
    val sublevels: List<Ranking>,
    val logo: String
)
