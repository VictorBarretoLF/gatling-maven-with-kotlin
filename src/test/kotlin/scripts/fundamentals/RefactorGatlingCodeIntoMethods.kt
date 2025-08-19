package scripts.fundamentals

import io.gatling.javaapi.core.ChainBuilder
import io.gatling.javaapi.http.HttpDsl
import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpProtocolBuilder

class RefactorGatlingCodeIntoMethods: Simulation() {

    private val BASE_URL = "https://www.videogamedb.uk/api"
    private val DEFAULT_HEADER = "application/json"

    private fun getAllVideoGames(): ChainBuilder =
       CoreDsl.exec(
           HttpDsl.http("Get All Video Games")
               .get("/videogame")
               .check(HttpDsl.status().not(404), HttpDsl.status().not(500))
       )

    private val getSpecificVideoGame: ChainBuilder =
        CoreDsl.exec(
            HttpDsl.http("Get specific Game")
                .get("/videogame/1")
                .check(HttpDsl.status().shouldBe(200))
        )

    // Step 1: Http Protocol Configuration
    private val httpProtocol: HttpProtocolBuilder = HttpDsl.http
        .baseUrl(BASE_URL)
        .acceptHeader(DEFAULT_HEADER)

    // Step 2: Define the scenario
    private val scenario = CoreDsl.scenario("Video Game DB - Section 5 code")
        .exec(getAllVideoGames())
        .pause(2L)
        .exec(getSpecificVideoGame)
        .pause(2L)
        .exec(getAllVideoGames())

    // Step 3: Load simulation
    init {
        setUp(
            scenario.injectOpen(CoreDsl.atOnceUsers(1))
        ).protocols(httpProtocol)
    }

}