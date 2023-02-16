package fr.amou.ffbad.tournaments.slack.bot.infra.driven.publication

import com.slack.api.Slack
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import fr.amou.ffbad.tournaments.slack.bot.domain.core.TournamentInfo
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentInfoDetails
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Publication
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.publication.config.SlackSettings
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class SlackPublication(val slack: Slack, val slackSettings: SlackSettings) : Publication {

    val logger = getLogger(SlackPublication::class.java)

    override fun publish(info: TournamentInfo, details: TournamentInfoDetails) {
        val authSlackClient = slack.methods(slackSettings.token)

        val newTournamentMessage = ChatPostMessageRequest.builder()
            .channel(slackSettings.channel)
            .text("Nouveau tournoi : ${info.name}")
            .blocks(buildTournamentSlackMessage(info, details))
            .build()
        val newTournamentResponse = authSlackClient.chatPostMessage(newTournamentMessage)
        logger.info(newTournamentResponse.toString())

        if (newTournamentResponse.isOk) {

            if (details.description.isNotBlank()) {
                val descriptionMessage = ChatPostMessageRequest.builder()
                    .channel(slackSettings.channel)
                    .threadTs(newTournamentResponse.ts)
                    .blocks(buildDescriptionSlackMessage(details.description))
                    .text("Message des organisateurs")
                    .build()
                authSlackClient.chatPostMessage(descriptionMessage)
            }

            val particularRulesMessage = ChatPostMessageRequest.builder()
                .channel(slackSettings.channel)
                .threadTs(newTournamentResponse.ts)
                .blocks(buildParticularRulesSlackMessage(details))
                .text("Toutes les infos : ${details.document.url}")
                .build()
            authSlackClient.chatPostMessage(particularRulesMessage)
        }
    }
}


