package fr.amou.ffbad.tournaments.slack.bot.infra.driven.publication

import com.slack.api.Slack
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import fr.amou.ffbad.tournaments.slack.bot.domain.core.TournamentInfo
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Publication
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.publication.config.SlackSettings
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class SlackPublication(val slack: Slack, val slackSettings: SlackSettings) : Publication {

    val logger = getLogger(SlackPublication::class.java)

    override fun publish(info: TournamentInfo) {
        val methods = slack.methods(slackSettings.token)

        val request = ChatPostMessageRequest.builder()
            .channel(slackSettings.channel)
            .text("Nouveau tournoi : ${info.name}")
            .blocks(info.toSlackMessage())
            .build()
        val response = methods.chatPostMessage(request)
        logger.info(response.toString())
    }
}


