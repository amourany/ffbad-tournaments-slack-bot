package fr.amou.ffbad.tournaments.slack.bot.domain.model

data class TournamentInfoDetails(
    val categories: List<String>,
    val description: String,
    val document: TournamentDocument,
    val isParabad: Boolean,
    val prices: List<TournamentPrice>
)

data class TournamentDocument(
    val type: String,
    val url: String
)

data class TournamentPrice(
    val price: Int,
    val registrationTable: Int
)
