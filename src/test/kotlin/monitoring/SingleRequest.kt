package monitoring

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl
import io.gatling.javaapi.http.HttpProtocolBuilder

class SingleRequest: Simulation() {

    private val BASE_URL = "http://localhost:3001/"

    // Step 1: Http Protocol Configuration
    private val httpProtocol: HttpProtocolBuilder = HttpDsl.http
        .baseUrl(BASE_URL)
        .acceptHeader("application/json")

    // Step 2: Define the scenario
    private val scenario = CoreDsl.scenario("My First Test")
        .exec(HttpDsl.http("Get Main Page")
            .get("/")
        )

    // Step 3: Load simulation
    init {
        setUp(
            scenario.injectOpen(CoreDsl.atOnceUsers(10))
        ).protocols(httpProtocol)
    }

}