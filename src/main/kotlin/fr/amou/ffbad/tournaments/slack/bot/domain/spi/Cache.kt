package fr.amou.ffbad.tournaments.slack.bot.domain.spi

interface Cache {
    fun save(competitionId: String, tournamentName: String, source: String)
    fun findAllByQuerySource(querySource: String): List<String>
}
