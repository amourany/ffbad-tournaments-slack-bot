package fr.amou.ffbad.tournaments.slack.bot.infra.driven.builder

import fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments.config.Organizer
import fr.amou.ffbad.tournaments.slack.bot.infra.driven.tournaments.config.RestTournament

fun aRestTournament(name: String = "A Tournament") = RestTournament(
    name = name,
    number = "1",
    discipline = "SH",
    distance = "12",
    startDate = "2024-07-13",
    endDate = "2024-07-14",
    limitDate = "2024-07-01",
    location = "Paris",
    sublevel = "P10, P11, P12, NC",
    organizer = Organizer(null, "CD75")
)
