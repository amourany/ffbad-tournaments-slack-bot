package fr.amou.ffbad.tournaments.slack.bot.domain.model

enum class AgeCategory(val value: String) {
    MINIBAD("Minibad"),
    POUSSIN("Poussin"),
    BENJAMIN("Benjamin"),
    MINIME("Minimes"),
    CADET("Cadet"),
    JUNIOR("Junior"),
    SENIOR("Sénior"),
    VETERAN("Vétéran"),
    ADULTES("Adultes");

    companion object {
        fun findFromValue(label: String): AgeCategory = entries.first { label.contains(it.value) }
    }
}
