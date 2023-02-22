package fr.amou.ffbad.tournaments.slack.bot.infra.driven.cache.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableDynamoDBRepositories(basePackages = ["fr.amou.ffbad.tournaments.slack.bot.infra.driven.cache"])
class DynamoDBConfig {
    @Value("\${amazon.dynamodb.endpoint}")
    private lateinit var amazonDynamoDBEndpoint: String

    @Value("\${amazon.aws.accesskey}")
    private lateinit var amazonAWSAccessKey: String

    @Value("\${amazon.aws.secretkey}")
    private lateinit var amazonAWSSecretKey: String

    @Bean
    fun amazonDynamoDB(): AmazonDynamoDB {
        val amazonDynamoDB: AmazonDynamoDBClientBuilder =
            AmazonDynamoDBClient.builder()
                .withCredentials(
                    AWSStaticCredentialsProvider(BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey))
                )

        if (amazonDynamoDBEndpoint.isNotBlank()) {
            amazonDynamoDB.setEndpointConfiguration(
                EndpointConfiguration(amazonDynamoDBEndpoint, "eu-west-3")
            )
        }
        return amazonDynamoDB
            .build()
    }

}
