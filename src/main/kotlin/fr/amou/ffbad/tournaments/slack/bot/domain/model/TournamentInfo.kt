package fr.amou.ffbad.tournaments.slack.bot.domain.model

import java.time.LocalDate

enum class AllowedDocumentsType(val value: String) {
    RULE_SET("Règlement particulier"),
    INVITATION("Plaquette d'invitation");

    companion object {
        fun findFromValue(label: String): AllowedDocumentsType = values().first { it.value == label }
    }
}

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
    val documents: List<TournamentDocument>,
    val isParabad: Boolean,
    val prices: List<TournamentPrice>,
    val organizer: String
)

data class TournamentDocument(
    val type: AllowedDocumentsType,
    val url: String
)

data class TournamentPrice(
    val price: Int,
    val registrationTable: Int
)
