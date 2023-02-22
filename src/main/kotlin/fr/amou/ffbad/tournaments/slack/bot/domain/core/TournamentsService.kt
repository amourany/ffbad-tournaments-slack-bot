package fr.amou.ffbad.tournaments.slack.bot.domain.core

import arrow.core.None
import arrow.core.Some
import arrow.core.filterOption
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Query
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Cache
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Publication
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Tournaments
import kotlinx.coroutines.*
import org.springframework.stereotype.Service
import java.time.LocalDate.now

@Service
class TournamentsService(
    private val tournaments: Tournaments, private val publication: Publication,
    private val cache: Cache
) {

    fun listTournaments(query: Query) {

        val alreadyPublishedTournaments = cache.findAll()

        runBlocking(Dispatchers.Default) {
            tournaments.find(query)
                .parallelMap { tournament ->
                    tournaments.details(tournament.competitionId).fold(
                        { None },
                        { details -> Some(Pair(tournament, details)) }
                    )
                }
                .filterOption()
                .asSequence()
                .filter { (tournaments) -> !alreadyPublishedTournaments.contains(tournaments.competitionId) }
                .filter { (tournaments) -> tournaments.joinLimitDate.isAfter(now()) }
                .filter { (_, details) -> !details.isParabad }
                .map { (tournament, details) ->
                    val isPublished = publication.publish(tournament, details)
                    Pair(isPublished, tournament)
                }
                .filter { (isPublished) -> isPublished }
                .toList()
                .forEach { (_, tournament) -> cache.save(tournament.competitionId, tournament.name) }
        }
    }
}

suspend fun <A, B> Iterable<A>.parallelMap(f: suspend (A) -> B): List<B> =
    coroutineScope { map { async { f(it) } }.awaitAll() }
