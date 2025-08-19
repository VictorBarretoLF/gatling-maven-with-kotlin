package scripts.feeder

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.FeederBuilder
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl
import io.gatling.javaapi.http.HttpProtocolBuilder

class JsonFeeder: Simulation() {

    private val BASE_URL = "https://www.videogamedb.uk/api"
    private val DEFAULT_HEADER = "application/json"

    // Step 1: Http Protocol Configuration
    private val httpProtocol: HttpProtocolBuilder = HttpDsl.http
        .baseUrl(BASE_URL)
        .acceptHeader(DEFAULT_HEADER)

    private val feeder:  FeederBuilder.FileBased<Any> =
        CoreDsl.jsonFile("data/gameJsonFile.json").circular()

    private val getSpecificGame =
        CoreDsl.feed(feeder)
            .exec(HttpDsl.http("Get video game with name - #{name}")
                .get("/videogame/#{id}")
                .check(CoreDsl.jmesPath("name").isEL("#{name}")))

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