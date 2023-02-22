package fr.amou.ffbad.tournaments.slack.bot.infra.driven.cache

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import fr.amou.ffbad.tournaments.slack.bot.domain.spi.Cache
import org.socialsignin.spring.data.dynamodb.repository.EnableScan
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
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

    constructor() : this("", "", "")
    constructor(id: String = randomUUID().toString(), competitionId: String, tournamentName: String) {
        this.id = id
        this.competitionId = competitionId
        this.tournamentName = tournamentName
    }
}

@Component
class DynamoDbCache(val repository: DynamoDbRepository) : Cache {
    override fun save(competitionId: String, tournamentName: String) {
        repository.save(PublishedTournament(competitionId = competitionId, tournamentName = tournamentName))
    }

    override fun findAll(): List<String> = repository.findAll().map { it.competitionId }
}

@Repository
@EnableScan
interface DynamoDbRepository : CrudRepository<PublishedTournament, Long>

