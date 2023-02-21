package fr.amou.ffbad.tournaments.slack.bot.domain.core

import arrow.core.None
import arrow.core.Some
import arrow.core.filterOption
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Query
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Publication
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Tournaments
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service
import java.time.LocalDate.now

@Service
class TournamentsService(
    private val tournaments: Tournaments, private val publication: Publication,
//    private val cache: Cache
) {

    val logger = getLogger(TournamentsService::class.java)

    fun listTournaments(query: Query) {
        logger.info("TournamentsService.listTournaments")
        runBlocking(Dispatchers.Default) {
            tournaments.find(query)
                .parallelMap { tournament ->
                    tournaments.details(tournament.competitionId).fold(
                        {
                            logger.info("TournamentsService.listTournaments.None")
                            None
                        },
                        { details ->
                            logger.info(details.toString())
                            Some(Pair(tournament, details)) }
                    )
                }
                .filterOption()
                .filter { (tournaments) -> tournaments.joinLimitDate.isAfter(now()) }
                .filter { (_, details) -> !details.isParabad }
                .forEach { (tournament, details) -> publication.publish(tournament, details) }
        }
    }
}

suspend fun <A, B> Iterable<A>.parallelMap(f: suspend (A) -> B): List<B> =
    coroutineScope { map { async { f(it) } }.awaitAll() }
