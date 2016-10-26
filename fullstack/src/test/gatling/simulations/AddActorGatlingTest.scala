import _root_.io.gatling.core.scenario.Simulation
import ch.qos.logback.classic.{Level, LoggerContext}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

/**
  * Performance test for the Actor entity.
  */
class AddActorGatlingTest extends Simulation {

    val context: LoggerContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
    // Log all HTTP requests
    //context.getLogger("io.gatling.http").setLevel(Level.valueOf("TRACE"))
    // Log failed HTTP requests
    //context.getLogger("io.gatling.http").setLevel(Level.valueOf("DEBUG"))

    val baseURL = Option(System.getProperty("baseURL")) getOrElse """http://127.0.0.1:8080"""

    val httpConf = http
        .baseURL(baseURL)
        .acceptHeader("*/*")
        .acceptEncodingHeader("gzip, deflate")
        .acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")
        .connection("keep-alive")
        .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0")

    val headers_http = Map(
        "Accept" -> """application/json"""
    )

    val headers_http_authenticated = Map(
        "Accept" -> """application/json""",
        "X-CSRF-TOKEN" -> "${csrf_token}"
    )

    val csvFeeder = csv("actors.csv").queue // 359712 entries

    val scn = scenario("Add Actor")
        .exec(http("First unauthenticated request")
            .get("/api/account")
            .headers(headers_http)
            .check(status.is(401))
            .check(headerRegex("Set-Cookie", "CSRF-TOKEN=(.*);[\\s]?[P,p]ath=/").saveAs("csrf_token"))).exitHereIfFailed
        .exec(http("Authentication")
            .post("/api/authentication")
            .headers(headers_http_authenticated)
            .formParam("j_username", "admin")
            .formParam("j_password", "admin")
            .formParam("remember-me", "true")
            .formParam("submit", "Login")).exitHereIfFailed
        .exec(http("Authenticated request")
            .get("/api/account")
            .headers(headers_http_authenticated)
            .check(status.is(200))
            .check(headerRegex("Set-Cookie", "CSRF-TOKEN=(.*);[\\s]?[P,p]ath=/").saveAs("csrf_token")))
        .repeat(3600) { // = 359712 / 100 rounded
            feed(csvFeeder)
                .exec(http("Create new actor")
                    .post("/api/actors")
                    .headers(headers_http_authenticated)
                    .body(StringBody("{\"firstName\":\"${firstName}\", \"lastName\":\"${lastName}\", \"birthDate\":\"${birthDate}\", \"birthLocation\":\"${birthLocation}\"}")).asJSON
                    .check(status.is(201)))
                .exitHereIfFailed

        }

    val users = scenario("Users").exec(scn)

    setUp(
        users.inject(atOnceUsers(100))
    ).protocols(httpConf)
}
