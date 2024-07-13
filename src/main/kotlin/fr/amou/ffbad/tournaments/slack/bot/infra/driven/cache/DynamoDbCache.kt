package fr.amou.ffbad.tournaments.slack.bot.infra.driven.cache

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Cache
import org.socialsignin.spring.data.dynamodb.repository.EnableScan
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.time.LocalDateTime.*
import java.util.UUID.randomUUID

@DynamoDBTable(tableName = "PublishedTournament")
class PublishedTournament {
    @DynamoDBHashKey
    @DynamoDBAttribute
    var id: String

    @DynamoDBAttribute
    var competitionId: String

    @DynamoDBAttribute
    var tournamentName: String

    @DynamoDBAttribute
    var publicationDate: String

    @DynamoDBAttribute
    var querySource: String

    constructor() : this("", "", "", "", "")
    constructor(
        id: String = randomUUID().toString(),
        competitionId: String,
        tournamentName: String,
        publicationDate: String = now().toString(),
        querySource: String
    ) {
        this.id = id
        this.competitionId = competitionId
        this.tournamentName = tournamentName
        this.publicationDate = publicationDate
        this.querySource = querySource
    }
}

@Component
class DynamoDbCache(val repository: DynamoDbRepository) : Cache {
    override fun save(competitionId: String, tournamentName: String, source: String) {
        repository.save(PublishedTournament(competitionId = competitionId, tournamentName = tournamentName, querySource = source))
    }

    override fun findAllByQuerySource(querySource: String): List<String> = repository.findAllByQuerySource(querySource).map { it.competitionId }
}

@Repository
@EnableScan
interface DynamoDbRepository : CrudRepository<PublishedTournament, Long> {
    fun findAllByQuerySource(querySource: String): List<PublishedTournament>
}
