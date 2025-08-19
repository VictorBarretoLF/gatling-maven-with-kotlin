package scripts.fundamentals

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl
import io.gatling.javaapi.http.HttpProtocolBuilder

class MyFirstTest: Simulation() {

    private val BASE_URL = "https://www.videogamedb.uk/api"
    private val DEFAULT_HEADER = "application/json"

    // Step 1: Http Protocol Configuration
    private val httpProtocol: HttpProtocolBuilder = HttpDsl.http
        .baseUrl(BASE_URL)
        .acceptHeader(DEFAULT_HEADER)

    // Step 2: Define the scenario
    private val scenario = CoreDsl.scenario("My First Test")
        .exec(HttpDsl.http("Get All Games")
            .get("/videogame")
        )

    // Step 3: Load simulation
    init {
        setUp(
            scenario.injectOpen(CoreDsl.atOnceUsers(1))
        ).protocols(httpProtocol)
    }

}