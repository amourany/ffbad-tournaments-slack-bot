package fr.amou.ffbad.tournaments.slack.bot.domain.model

enum class Disciplines(val shortName: String) {
    MEN_SINGLE("SH"),
    WOMEN_SINGLE("SD"),
    MEN_DOUBLE("DH"),
    WOMEN_DOUBLE("DD"),
    MIXTED_DOUBLE("DX");

    companion object {
        fun fromShortName(shortName: String) = values().first { it.shortName == shortName.trim() }
    }
}

