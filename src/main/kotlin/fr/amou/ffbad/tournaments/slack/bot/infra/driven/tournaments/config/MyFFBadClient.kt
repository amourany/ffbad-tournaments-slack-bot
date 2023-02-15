package fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments.config

import fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments.RestQuery
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

data class RestTournamentsResponse(
    val totalPage: Int,
    val currentPage: Int,
    val tournaments: List<RestTournament>
)

data class RestTournament(
    val name: String,
    val competitionId: String,
    val discipline: String,
    val startDate: String,
    val endDate: String,
    val limitDate: String,
    val location: String,
    val sublevel: String,
    val organizer: Organizer
)

data class Organizer(val logo: String?)

interface MyFFBadClient {

    @POST("/api/search")
    fun findTournaments(@HeaderMap headers: Map<String, String>, @Body query: RestQuery): Call<RestTournamentsResponse>
}


