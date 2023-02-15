package fr.amou.ffbad.tournaments.slack.bot.domain.builder

import fr.amou.ffbad.tournaments.slack.bot.domain.core.TournamentInfo
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Disciplines
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Disciplines.*
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Ranking
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Ranking.*
import java.time.LocalDate

fun aTournament(
    competitionId: String = "1234",
    name: String = "Tournoi de Malakoff",
    disciplines: List<Disciplines> = listOf(MEN_DOUBLE, WOMEN_DOUBLE, MIXTED_DOUBLE),
    startDate: LocalDate = LocalDate.parse("2023-04-05"),
    endDate: LocalDate = LocalDate.parse("2023-04-06"),
    joinLimitDate: LocalDate = LocalDate.parse("2023-03-04"),
    location: String = "Malakoff",
    sublevels: List<Ranking> = listOf(D9, P10, P11, P12, NC),
    logo: String = "https://pbs.twimg.com/profile_images/625633822235693056/lNGUneLX_400x400.jpg"
) =
    TournamentInfo(
        competitionId = competitionId,
        name = name,
        disciplines = disciplines,
        startDate = startDate,
        endDate = endDate,
        joinLimitDate = joinLimitDate,
        location = location,
        sublevels = sublevels,
        logo = logo
    )
