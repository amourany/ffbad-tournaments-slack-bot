package fr.amou.ffbad.tournaments.slack.bot.domain.core

import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Cache
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Publication
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Tournaments
import org.springframework.stereotype.Service
import java.time.LocalDate.now

@Service
class TournamentsService(
    private val tournaments: Tournaments, private val publication: Publication,
    private val cache: Cache
) {

    fun listTournaments() {

        val alreadyPublishedTournaments = cache.findAll()

        tournaments.findAll()
            .filter { !alreadyPublishedTournaments.contains(it.competitionId) }
            .filter { it.joinLimitDate.isAfter(now()) }
            .filter { !it.isParabad }
            .map {
                val isPublished = publication.publish(it)
                Pair(isPublished, it)
            }
            .filter { (isPublished) -> isPublished }
            .forEach { (_, tournament) -> cache.save(tournament.competitionId, tournament.name) }
    }
}
