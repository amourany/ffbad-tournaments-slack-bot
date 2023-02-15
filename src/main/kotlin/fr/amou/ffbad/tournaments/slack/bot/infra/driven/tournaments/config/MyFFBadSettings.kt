package fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "myffbad")
data class MyFFBadSettings(
    val url: String,
)
