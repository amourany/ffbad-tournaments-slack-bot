package fr.amou.ffbad.tournaments.slack.bot

import com.github.tomakehurst.wiremock.WireMockServer
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.test.TestCase
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(initializers = [WireMockContextInitializer::class])
@ConfigurationPropertiesScan
@SpringBootTest(
    classes = [Application::class],
    properties = ["spring.main.allow-bean-definition-overriding=true"],
    webEnvironment = RANDOM_PORT,
)
@ActiveProfiles(value = ["test"])
@TestInstance(PER_CLASS)
@Ignored
class IntegrationSpec(body: ShouldSpec.() -> Unit) : ShouldSpec(body) {

    @Autowired
    private lateinit var wireMockServer: WireMockServer

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)

        if (this::wireMockServer.isInitialized) {
            wireMockServer.resetAll()
        }
    }
}
