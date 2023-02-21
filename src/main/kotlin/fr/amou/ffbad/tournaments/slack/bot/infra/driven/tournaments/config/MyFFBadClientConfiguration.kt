package fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments.config

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.X509TrustManager


@Configuration
class MyFFBadClientConfiguration(val myFFBadSettings: MyFFBadSettings) {

    val logger = getLogger(MyFFBadClientConfiguration::class.java)

    @Bean
    @Primary
    fun objectMapper(): ObjectMapper =
        ObjectMapper()
            .disable(FAIL_ON_UNKNOWN_PROPERTIES)
            .findAndRegisterModules()
            .registerModule(KotlinModule.Builder().build())

    @Bean
    fun myFFBadClientClient(objectMapper: ObjectMapper): MyFFBadClient {
        val retrofit = Retrofit.Builder()
            .baseUrl(myFFBadSettings.url)
            .client(createOkHttpClient()!!)
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .build()

        return retrofit.create(MyFFBadClient::class.java)
    }

    private fun createOkHttpClient(): OkHttpClient? {

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HEADERS)
        logging.setLevel(BODY)

        return try {
            val trustAllCerts = arrayOf<X509TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
            )

            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0])
                .hostnameVerifier { _: String?, _: SSLSession? -> true }
//                .addInterceptor(logging)
                .build()
        } catch (e: Exception) {
            logger.error(e.message, e)
            throw RuntimeException(e)
        }
    }
}
