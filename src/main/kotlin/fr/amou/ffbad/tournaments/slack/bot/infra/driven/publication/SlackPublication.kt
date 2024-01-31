package fr.amou.ffbad.tournaments.slack.bot.infra.driven.publication

import com.slack.api.Slack
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentInfo
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Publication
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.publication.config.SlackSettings
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class SlackPublication(val slack: Slack, val slackSettings: SlackSettings) : Publication {

    val logger = getLogger(SlackPublication::class.java)

    override fun publish(info: TournamentInfo): Boolean {
        val authSlackClient = slack.methods(slackSettings.token)

        val newTournamentMessage = ChatPostMessageRequest.builder()
            .channel(slackSettings.channel)
            .text("Nouveau tournoi : ${info.name}")
            .blocks(buildTournamentSlackMessage(info))
            .build()
        val newTournamentResponse = authSlackClient.chatPostMessage(newTournamentMessage)

        if (newTournamentResponse.isOk) {

            if (info.description.isNotBlank()) {
                val descriptionMessage = ChatPostMessageRequest.builder()
                    .channel(slackSettings.channel)
                    .threadTs(newTournamentResponse.ts)
                    .blocks(buildDescriptionSlackMessage(info.description))
                    .text("Message des organisateurs")
                    .build()
                authSlackClient.chatPostMessage(descriptionMessage)
            }

            info.documents.map { document ->
                ChatPostMessageRequest.builder()
                    .channel(slackSettings.channel)
                    .threadTs(newTournamentResponse.ts)
                    .blocks(buildDocumentSlackMessage(document))
                    .text("${document.type.value} : ${document.url}")
                    .build()
            }.forEach {
                authSlackClient.chatPostMessage(it)
            }

        } else {
            logger.error(newTournamentResponse.toString())
        }

        return newTournamentResponse.isOk
    }

    override fun publishError(stackTrace: String) {
        val authSlackClient = slack.methods(slackSettings.token)

        val errorMessage = ChatPostMessageRequest.builder()
            .channel(slackSettings.channel)
            .text(":robot_face: Erreur en récupérant les tournois")
            .blocks(buildErrorSlackMessage())
            .build()

        val errorResponse = authSlackClient.chatPostMessage(errorMessage)
        if (errorResponse.isOk) {
            val stackMessage = ChatPostMessageRequest.builder()
                .channel(slackSettings.channel)
                .threadTs(errorResponse.ts)
                .text("Détails de l'erreur")
                .blocks(buildStackSlackMessage(stackTrace))
                .build()
            authSlackClient.chatPostMessage(stackMessage)
        }
    }
}


