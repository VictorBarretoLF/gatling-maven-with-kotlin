package scripts.feeder

import io.gatling.javaapi.core.ChainBuilder
import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.FeederBuilder
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl
import io.gatling.javaapi.http.HttpProtocolBuilder
import java.time.Duration

class CsvFeeder: Simulation() {

    private val BASE_URL = "https://www.videogamedb.uk/api"
    private val DEFAULT_HEADER = "application/json"

    // Step 1: Http Protocol Configuration
    private val httpProtocol: HttpProtocolBuilder = HttpDsl.http
        .baseUrl(BASE_URL)
        .acceptHeader(DEFAULT_HEADER)

    private val feeder:  FeederBuilder.FileBased<String> =
        CoreDsl.csv("data/gameCsvFile.csv").circular()

    private val getSpecificGame: ChainBuilder =
        CoreDsl.feed(feeder)
            .exec(
                HttpDsl.http("Get video game with name - #{gameName}")
                    .get("/videogame/#{gameId}")
                    .check(HttpDsl.status().shouldBe(200))
                    .check(CoreDsl.jmesPath("name").isEL("#{gameName}"))
            )

    // Step 2: Define the scenario
    private val scenario = CoreDsl.scenario("Video Game Db - Section 6 code")
        .repeat(10).on(
            CoreDsl.exec(getSpecificGame)
                .pause(1)
        );

    // Step 3: Load simulation
    init {
        setUp(
            scenario.injectOpen(CoreDsl.atOnceUsers(1))
        ).protocols(httpProtocol)
    }

}