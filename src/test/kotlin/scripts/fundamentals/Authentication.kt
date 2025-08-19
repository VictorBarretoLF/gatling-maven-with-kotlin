package scripts.fundamentals

import io.gatling.javaapi.core.CoreDsl.StringBody
import io.gatling.javaapi.core.ChainBuilder
import io.gatling.javaapi.http.HttpDsl
import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpProtocolBuilder

class Authentication: Simulation() {

    private val BASE_URL = "https://www.videogamedb.uk/api"
    private val DEFAULT_HEADER = "application/json"

    private val authenticate = CoreDsl.exec(
        HttpDsl.http("Authenticate")
            .post("/authenticate")
            .body(StringBody("""
                    {
                      "password": "admin",
                      "username": "admin"
                    }
                """))
            .check(CoreDsl.jmesPath("token").saveAs("jwtToken"))
    )

    private val createNewGame: ChainBuilder =
        CoreDsl.exec(
            HttpDsl.http("Create New Game")
                .post("/videogame")
                .header("Authorization", "Bearer #{jwtToken}")
                .body(StringBody("""
                        {
                          "category": "Platform",
                          "name": "Mario",
                          "rating": "Mature",
                          "releaseDate": "2012-05-04",
                          "reviewScore": 85
                        }
                    """))
        )

    // Step 1: Http Protocol Configuration
    private val httpProtocol: HttpProtocolBuilder = HttpDsl.http
        .baseUrl(BASE_URL)
        .acceptHeader(DEFAULT_HEADER)
        .contentTypeHeader(DEFAULT_HEADER)

    // Step 2: Define the scenario
    private val scenario = CoreDsl.scenario("Video Game DB - Section 5 code")
        .exec(authenticate)
        .exec(createNewGame)

    // Step 3: Load simulation
    init {
        setUp(
            scenario.injectOpen(CoreDsl.atOnceUsers(1))
        ).protocols(httpProtocol)
    }

}