package fr.amou.ffbad.tournaments.slack.bot.infra.driven

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MockCall<T>(private val response: Response<T>): Call<T> {
    override fun clone(): Call<T> = this

    override fun execute(): Response<T> = response

    override fun isExecuted(): Boolean = false

    override fun cancel() {}

    override fun isCanceled(): Boolean = false

    override fun request(): Request = Request.Builder().build()

    override fun timeout(): Timeout = Timeout.NONE

    override fun enqueue(p0: Callback<T>) {}
}
