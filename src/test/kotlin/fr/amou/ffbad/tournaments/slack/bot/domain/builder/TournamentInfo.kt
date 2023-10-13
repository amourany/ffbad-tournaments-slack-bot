package fr.amou.ffbad.tournaments.slack.bot.domain.builder

import fr.amou.ffbad.tournaments.slack.bot.domain.model.*
import fr.amou.ffbad.tournaments.slack.bot.domain.model.AllowedDocumentsType.RULE_SET
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Disciplines.*
import fr.amou.ffbad.tournaments.slack.bot.domain.model.Ranking.*
import java.time.LocalDate
import java.time.LocalDate.now

fun aTournament(
    competitionId: String = "1234",
    name: String = "Tournoi de Malakoff",
    disciplines: List<Disciplines> = listOf(MEN_DOUBLE, WOMEN_DOUBLE, MIXED_DOUBLE),
    distance: Int = 5,
    dates: List<LocalDate> = listOf(LocalDate.of(2023, 4, 5), LocalDate.of(2023, 4, 6)),
    joinLimitDate: LocalDate = now().plusDays(1),
    location: String = "Malakoff",
    sublevels: List<Ranking> = listOf(D9, P10, P11, P12, NC),
    logo: String = "https://pbs.twimg.com/profile_images/625633822235693056/lNGUneLX_400x400.jpg",
    categories: List<String> = listOf("Adultes"),
    description: String = "",
    documents: List<TournamentDocument> = listOf(TournamentDocument(type = RULE_SET, url = "some-url")),
    isParabad: Boolean = false,
    prices: List<TournamentPrice> = listOf(TournamentPrice(price = 10, registrationTable = 1)),
    organizer: String = "USMM92"
) =
    TournamentInfo(
        competitionId = competitionId,
        name = name,
        disciplines = disciplines,
        distance = distance,
        dates = dates,
        joinLimitDate = joinLimitDate,
        location = location,
        sublevels = sublevels,
        logo = logo,
        categories = categories,
        description = description,
        documents = documents,
        isParabad = isParabad,
        prices = prices,
        organizer = organizer
    )

