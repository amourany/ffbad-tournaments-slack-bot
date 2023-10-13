package fr.amou.ffbad.tournaments.slack.bot.infra.driven.publication

import com.slack.api.model.block.HeaderBlock
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.model.block.composition.PlainTextObject
import com.slack.api.model.block.element.ButtonElement
import com.slack.api.model.block.element.ImageElement
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Disciplines
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Disciplines.*
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentDocument
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentInfo
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ofPattern
import java.util.Locale.FRENCH

fun buildTournamentSlackMessage(info: TournamentInfo): List<LayoutBlock> {

    val dateLine = ":calendar: : ${info.dates.joinToString(", ") { it.toFrenchDate() }}"
    val locationLine = ":round_pushpin: : ${info.location} (${info.distance}km)"
    val disciplinesLine = ":busts_in_silhouette: : ${info.disciplines.joinToString(", ") { it.toSlackMessage() }}"
    val rankingLine = ":chart_with_upwards_trend: : ${info.sublevels.joinToString(", ")}"
    val pricesLine = ":euro: : ${
        info.prices.joinToString(", ")
        { "${it.price}€ pour ${it.registrationTable} tableau${if (it.registrationTable > 1) "x" else ""}" }
    }"
    val registrationEndLine = ":stopwatch: : Inscriptions jusqu'au ${info.joinLimitDate.toFrenchDate()}"
    val moreInfosLine = "Pour plus d'infos et vous organiser :arrow_heading_down:"

    val messageContent =
        listOf(dateLine, locationLine, disciplinesLine, rankingLine, pricesLine, registrationEndLine, moreInfosLine)

    return listOf(
        HeaderBlock.builder().text(PlainTextObject.builder().text(info.name).build()).build(),
        SectionBlock.builder().text(PlainTextObject.builder().text(messageContent.joinToString("\n\n")).build())
            .accessory(ImageElement.builder().imageUrl(info.logo).altText("Logo").build()).build(),
    )
}

fun buildDocumentSlackMessage(document: TournamentDocument): List<LayoutBlock> {
    return listOf(
        SectionBlock.builder()
            .text(PlainTextObject.builder().text("${document.type.value} : ").build())
            .accessory(
                ButtonElement.builder()
                    .text(PlainTextObject.builder().text(document.type.value).build())
                    .url(document.url).build()
            )
            .build(),
    )
}

fun buildDescriptionSlackMessage(description: String): List<LayoutBlock> {
    return listOf(
        SectionBlock.builder()
            .text(PlainTextObject.builder().text("Un mot des organisateurs : ").build())
            .build(),
        SectionBlock.builder()
            .text(MarkdownTextObject.builder().text("> ${description.replace("\n", "\n> ")}").build())
            .build()
    )
}

fun LocalDate.toFrenchDate(): String = this.format(ofPattern("EEEE dd MMMM", FRENCH))

fun Disciplines.toSlackMessage(): String = when (this) {
    MEN_SINGLE -> ":man_standing: Simple Hommes"
    WOMEN_SINGLE -> ":woman_standing: Simple Dames"
    MEN_DOUBLE -> ":men_holding_hands: Double Hommes"
    WOMEN_DOUBLE -> ":women_holding_hands: Double Dames"
    MIXED_DOUBLE -> ":woman_and_man_holding_hands: Double Mixte"
}
