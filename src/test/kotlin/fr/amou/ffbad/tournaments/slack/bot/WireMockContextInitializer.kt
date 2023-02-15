package fr.amou.ffbad.tournaments.slack.bot

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.event.ContextClosedEvent

class WireMockContextInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    private val logger = LoggerFactory.getLogger(WireMockContextInitializer::class.java)

    override fun initialize(applicationContext: ConfigurableApplicationContext) {

        val wireMockServer = WireMockServer(
                WireMockConfiguration()
                        .port(8989)
                        .usingFilesUnderDirectory(System.getProperty("user.dir")+"/src/test/resources/mock")
        )
        wireMockServer.start()

        logger.info("Starting wiremock on port ${wireMockServer.port()}")
        applicationContext.beanFactory.registerSingleton("wireMockServer", wireMockServer)

        applicationContext.addApplicationListener { applicationEvent ->
            if (applicationEvent is ContextClosedEvent) {
                wireMockServer.stop()
            }
        }
    }
}
