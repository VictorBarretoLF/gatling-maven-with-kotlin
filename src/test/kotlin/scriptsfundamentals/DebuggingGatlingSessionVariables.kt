package scriptsfundamentals

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl
import io.gatling.javaapi.http.HttpProtocolBuilder
import java.time.Duration

class DebuggingGatlingSessionVariables: Simulation() {

    private val BASE_URL = "https://www.videogamedb.uk/api"
    private val DEFAULT_HEADER = "application/json"

    // Step 1: Http Protocol Configuration
    private val httpProtocol: HttpProtocolBuilder = HttpDsl.http
        .baseUrl(BASE_URL)
        .acceptHeader(DEFAULT_HEADER)

    // Step 2: Define the scenario
    private val scenario = CoreDsl.scenario("Video Game DB - Section 5 code")

        .exec(HttpDsl.http("Get specific Game")
            .get("/videogame/1")
            .check(HttpDsl.status().within(200, 201, 202))
            .check(CoreDsl.jmesPath("name").shouldBe("Resident Evil 4"))
        )

        .exec(HttpDsl.http("Get All Video Games")
            .get("/videogame")
            .check(HttpDsl.status().not(404), HttpDsl.status().not(500))
            .check(CoreDsl.jmesPath("[1].id").saveAs("gameId"))
        )

        .exec{ session ->
            println("SESSION HERE: $session")
            println("game set to: ${session.getString("gameId")}")

            session
        }

        .exec(HttpDsl.http("Get specific game with Id - #{gameId}")
            .get("/videogame/#{gameId}")
            .check(HttpDsl.status().shouldBe(200))
            .check(CoreDsl.jmesPath("name").shouldBe("Gran Turismo 3"))
            .check(CoreDsl.bodyString().saveAs("responseBody"))
        )
        .exec { session ->
            println("RESPONSE BODY: ${session.getString("responseBody")}")
            session
        }

    // Step 3: Load simulation
    init {
        setUp(
            scenario.injectOpen(CoreDsl.atOnceUsers(1))
        ).protocols(httpProtocol)
    }

}