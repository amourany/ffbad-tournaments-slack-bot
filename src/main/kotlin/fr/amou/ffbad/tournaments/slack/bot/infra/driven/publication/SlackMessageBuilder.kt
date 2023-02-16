package fr.amou.ffbad.tournaments.slack.bot.infra.driven.publication

import com.slack.api.model.block.HeaderBlock
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.model.block.composition.PlainTextObject
import com.slack.api.model.block.element.ButtonElement
import com.slack.api.model.block.element.ImageElement
import fr.amou.ffbad.tournaments.slack.bot.domain.core.TournamentInfo
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Disciplines
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Disciplines.*
import fr.amou.ffbad.tournaments.slack.bot.domain.model.TournamentInfoDetails
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale.FRENCH

fun buildTournamentSlackMessage(info: TournamentInfo, details: TournamentInfoDetails): List<LayoutBlock> {

    val dateLine = PlainTextObject.builder()
        .text(":calendar: : ${dateToFrenchDate(info.startDate)} et ${dateToFrenchDate(info.endDate)}")
        .build()

    val locationLine = PlainTextObject.builder().text(":round_pushpin: : ${info.location}").build()

    val disciplinesLine = PlainTextObject.builder()
        .text(":busts_in_silhouette: : ${info.disciplines.joinToString(", ") { it.toSlackMessage() }}")
        .build()

    val rankingLine =
        MarkdownTextObject.builder().text(":chart_with_upwards_trend: : ${info.sublevels.joinToString(", ")}").build()

    val pricesLine =
        PlainTextObject.builder()
            .text(":euro: : ${
                details.prices.joinToString(", ")
                { "${it.price}€ pour ${it.registrationTable} tableau${if (it.registrationTable > 1) "x" else ""}" }
            }"
            )
            .build()

    return listOf(
        HeaderBlock.builder().text(PlainTextObject.builder().text(info.name).build()).build(),
        SectionBlock.builder().text(PlainTextObject.builder().text(" ").build())
            .accessory(ImageElement.builder().imageUrl(info.logo).altText("Logo").build()).build(),
        SectionBlock.builder().text(dateLine).build(),
        SectionBlock.builder().text(locationLine).build(),
        SectionBlock.builder().text(disciplinesLine).build(),
        SectionBlock.builder().text(rankingLine).build(),
        SectionBlock.builder().text(pricesLine).build()
    )
}

fun buildParticularRulesSlackMessage(details: TournamentInfoDetails): List<LayoutBlock> {
    return listOf(
        SectionBlock.builder()
            .text(PlainTextObject.builder().text("Toutes les infos : ").build())
            .accessory(
                ButtonElement.builder()
                    .text(PlainTextObject.builder().text(details.document.type).build())
                    .url(details.document.url).build()
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

fun dateToFrenchDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM", FRENCH)
    return date.format(formatter)
}

fun Disciplines.toSlackMessage(): String = when (this) {
    MEN_SINGLE -> ":man_standing: Simple homme"
    WOMEN_SINGLE -> ":woman_standing: Simple dame"
    MEN_DOUBLE -> ":men_holding_hands: Double homme"
    WOMEN_DOUBLE -> ":women_holding_hands: Double dame"
    MIXTED_DOUBLE -> ":woman_and_man_holding_hands: Double mixte"
}
