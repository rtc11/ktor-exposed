import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction
import route.Authority

internal class KtorTestApp : FeatureSpec({

    feature("route /") {
        scenario("should return Hello") {
            withTestApplication(Application::module) {
                with(handleRequest(HttpMethod.Get, "/")) {
                    response.status() shouldBe HttpStatusCode.OK
                    response.content shouldBe "Hello"
                }
            }
        }
    }

    feature("route /authorities/test") {
        scenario("should return authority when present in db") {
            withTestApplication(Application::module) {

                transaction {
                    database.dao.AuthorityId.new("1") {
                        authority = database.dao.Authority.new("test") {}
                    }
                }

                with(handleRequest(HttpMethod.Get, "/authorities/test")) {
                    val authorities = Json.decodeFromString<List<Authority>>(response.content!!)
                    authorities shouldHaveSize 1
                    authorities.first().name shouldBe "test"
                }
            }
        }
    }
})
