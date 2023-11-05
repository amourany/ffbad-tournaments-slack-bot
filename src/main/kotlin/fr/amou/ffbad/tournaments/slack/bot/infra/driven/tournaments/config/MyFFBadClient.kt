package fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments.config

import fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments.RestQuery
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path

data class RestTournamentsResponse(
    val totalPage: Int,
    val currentPage: Int,
    val tournaments: List<RestTournament>
)

data class RestTournament(
    val name: String,
    val number: String,
    val discipline: String,
    val distance: String,
    val startDate: String,
    val endDate: String,
    val limitDate: String,
    val location: String,
    val sublevel: String,
    val organizer: Organizer
)

data class Organizer(val logo: String?, val initials: String)

data class RestTournamentDetailsResponse(
    val categories: List<String>,
    val description: String? = "",
    val documents: List<RestTournamentDocument>,
    val isParabad: Boolean,
    val prices: List<RestTournamentPrice>
)

data class RestTournamentDocument(
    val type: String,
    val url: String
)

data class RestTournamentPrice(
    val price: Int,
    val registrationTable: Int
)

interface MyFFBadClient {

    @POST("/api/search")
    fun findTournaments(@HeaderMap headers: Map<String, String>, @Body query: RestQuery): Call<RestTournamentsResponse>

    @POST("api/tournament/{id}/informations")
    fun findTournamentDetails(@HeaderMap headers: Map<String, String>, @Path("id") id: String): Call<RestTournamentDetailsResponse>
}



