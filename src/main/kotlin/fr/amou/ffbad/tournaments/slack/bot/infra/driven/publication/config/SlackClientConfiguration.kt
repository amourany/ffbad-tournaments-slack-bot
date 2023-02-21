package fr.amou.ffbad.tournaments.slack.bot.infra.driven.publication.config

import com.slack.api.Slack
import com.slack.api.SlackConfig
import com.slack.api.util.http.listener.ResponsePrettyPrintingListener
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SlackClientConfiguration {

    val logger = getLogger(SlackClientConfiguration::class.java)

    @Bean
    fun slack(): Slack {
        logger.info("Instantiating Slack client")
        val config = SlackConfig()
        config.httpClientResponseHandlers.add(ResponsePrettyPrintingListener())
        return Slack.getInstance(config)
    }
}
