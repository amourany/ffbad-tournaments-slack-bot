package fr.amou.ffbad.tournaments.slack.bot.infra.driven.publication.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "slack")
data class SlackSettings(val token: String, val channel: String)
