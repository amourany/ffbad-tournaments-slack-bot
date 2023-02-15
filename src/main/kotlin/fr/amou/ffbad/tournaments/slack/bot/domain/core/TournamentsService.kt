package fr.amou.ffbad.tournaments.slack.bot.domain.core

import fr.amou.ffbad.tournaments.slack.bot.domain.model.Disciplines
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Query
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Ranking
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Publication
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Tournaments
import org.springframework.stereotype.Service
import java.time.LocalDate

data class TournamentInfo(
    val competitionId: String,
    val name: String,
    val disciplines: List<Disciplines>,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val joinLimitDate: LocalDate,
    val location: String,
    val sublevels: List<Ranking>,
    val logo: String
)

@Service
class TournamentsService(private val tournaments: Tournaments, private val publication: Publication) {
    fun listTournaments(query: Query): List<TournamentInfo> {
        val tournamentInfos = tournaments.find(query)
        if (tournamentInfos.isNotEmpty()) {
            publication.publish(tournamentInfos.first())
        }
        return tournamentInfos
    }
}
