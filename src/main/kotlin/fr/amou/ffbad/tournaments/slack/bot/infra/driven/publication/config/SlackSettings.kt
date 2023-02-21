package fr.amou.ffbad.tournaments.slack.bot.infra.driven.publication.config

import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "slack")
data class SlackSettings(val token: String, val channel: String) {
    init {
        val logger = getLogger(SlackSettings::class.java)
        logger.info("Target slack channel : $channel")
    }
}
