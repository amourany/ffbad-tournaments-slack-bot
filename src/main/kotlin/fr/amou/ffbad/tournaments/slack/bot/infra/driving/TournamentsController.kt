package fr.amou.ffbad.tournaments.slack.bot.infra.driving

import arrow.core.Either
import arrow.core.Either.*
import arrow.core.flatten
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.*
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.amou.ffbad.tournaments.slack.bot.domain.api.ListTournaments
import fr.amou.ffbad.tournaments.slack.bot.domain.model.AgeCategory
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Ranking
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentSearchQuery
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.function.Function

@JsonAutoDetect(fieldVisibility = ANY)
data class JsonSearchQuery(
    @JsonProperty("zipCode")
    val zipCode: String,
    @JsonProperty("distance")
    val distance: Int,
    @JsonProperty("subLevels")
    val subLevels: List<String>,
    @JsonProperty("categories")
    val categories: List<String>,
)

@Component
class TournamentsController(val listTournaments: ListTournaments) {

    val logger = LoggerFactory.getLogger(TournamentsController::class.java)

    @Bean
    fun run(): Function<String, String> {
        return Function<String, String> { searchParams ->

            val query: JsonSearchQuery = ObjectMapper().readValue(searchParams)

            validateSearchQuery(query).fold(
                { errors -> errors.forEach { logger.error(it) } },
                {
                    val tournamentSearchQuery = TournamentSearchQuery(
                        zipCode = query.zipCode,
                        distance = query.distance,
                        subLevels = query.subLevels.map { Ranking.valueOf(it) },
                        categories = query.categories.map { AgeCategory.valueOf(it) }
                    )
                    listTournaments.from(tournamentSearchQuery)
                }
            )
            "OK"
        }
    }

    fun validateSearchQuery(query: JsonSearchQuery): Either<List<String>, Unit> {
        val errors = listOf(
            validateZipCode(query.zipCode),
            validateSubLevels(query.subLevels),
            validateAgeCategories(query.categories)
        )
            .flatten()

        return when (errors.isEmpty()) {
            true -> Right(Unit)
            false -> Left(errors)
        }
    }

    fun validateZipCode(zipCode: String): List<String> = when (zipCode.isBlank()) {
        true -> listOf("ZipCode cannot be blank")
        false -> emptyList()
    }

    fun validateSubLevels(subLevels: List<String>): List<String> {
        val subLevelsNames = Ranking.values().map { it.name }
        return subLevels.filter { !subLevelsNames.contains(it) }.map { "Unknown sub-level : $it" }
    }

    fun validateAgeCategories(ageCategories: List<String>): List<String> {
        val agesCategoryNames = AgeCategory.values().map { it.name }
        return ageCategories.filter { !agesCategoryNames.contains(it) }.map { "Unknown age category : $it" }
    }
}
