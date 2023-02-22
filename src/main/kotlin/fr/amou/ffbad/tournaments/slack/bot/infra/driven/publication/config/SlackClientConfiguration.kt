package fr.amou.ffbad.tournaments.slack.bot.infra.driven.publication.config

import com.slack.api.Slack
import com.slack.api.SlackConfig
import com.slack.api.util.http.listener.ResponsePrettyPrintingListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SlackClientConfiguration {

    @Bean
    fun slack(): Slack {
        val config = SlackConfig()
        config.httpClientResponseHandlers.add(ResponsePrettyPrintingListener())
        return Slack.getInstance(config)
    }
}
