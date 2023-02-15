package fr.amou.ffbad.tournaments.slack.bot.infra.driven.publication

import com.slack.api.model.block.HeaderBlock
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.model.block.composition.PlainTextObject
import com.slack.api.model.block.composition.TextObject
import com.slack.api.model.block.element.ImageElement
import fr.amou.ffbad.tournaments.slack.bot.domain.core.TournamentInfo
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Disciplines
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Disciplines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

fun TournamentInfo.toSlackMessage(): List<LayoutBlock> {

    val dateAndLocationLine = listOf<TextObject>(
        PlainTextObject.builder().text(":calendar: : ${dateToFrenchDate(startDate)} et ${dateToFrenchDate(endDate)}")
            .build(),
        PlainTextObject.builder().text(":round_pushpin: : $location").build()
    )

    val disciplinesLine = PlainTextObject.builder()
        .text(":busts_in_silhouette: : ${disciplines.joinToString(", ") { it.toSlackMessage() }}")
        .build()

    val rankingLine =
        MarkdownTextObject.builder().text("*Classements* : ${sublevels.joinToString(", ")}").build()

    return listOf(
        HeaderBlock.builder().text(PlainTextObject.builder().text(name).build()).build(),
        SectionBlock.builder().fields(dateAndLocationLine)
            .accessory(ImageElement.builder().imageUrl(logo).altText("Logo").build()).build(),
        SectionBlock.builder().text(disciplinesLine).build(),
        SectionBlock.builder().text(rankingLine).build()
    )
}

fun dateToFrenchDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMMM", Locale.FRENCH)
    return date.format(formatter)
}

fun Disciplines.toSlackMessage(): String = when (this) {
    MEN_SINGLE -> ":man_standing: Simple homme"
    WOMEN_SINGLE -> ":woman_standing: Simple dame"
    MEN_DOUBLE -> ":men_holding_hands: Double homme"
    WOMEN_DOUBLE -> ":women_holding_hands: Double dame"
    MIXTED_DOUBLE -> ":woman_and_man_holding_hands: Double mixte"
}
