package fr.amou.ffbad.tournaments.slack.bot.infra.driven

import org.slf4j.Logger
import retrofit2.Call
import retrofit2.Response

data class OnFailure<U>(val defaultValue: U, val message: String)

fun <T, U> restCall(call: Call<T>, onSuccess: (response: Response<T>) -> U, onFailure: OnFailure<U>) = { logger: Logger ->
    val (fallback, message) = onFailure
    try {
        val response = call.execute()
        if (response.isSuccessful) {
            onSuccess(response)
        } else {
            logger.error("$message, {}", response.errorBody()?.string())
            fallback
        }
    } catch (exception: Exception) {
        logger.error(message, exception)
        fallback
    }
}

