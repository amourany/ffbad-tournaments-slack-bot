package fr.amou.ffbad.tournaments.slack.bot.domain.model

data class TournamentSearchQuery(
    val zipCode: String,
    val distance: Int,
    val subLevels: List<Ranking>,
    val categories: List<AgeCategory>,
)
