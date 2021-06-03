import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import database.dao.Authority
import database.dao.AuthorityId
import database.dsl.findAuthorities

internal class DslAndDaoTest : StringSpec({
    val h2 = TestDb.h2()

    "Dao Authority inserted can be fetched with Dsl Authority" {
        h2.withStatement {
            AuthorityId.new("RUT:Authority:RUT") {
                authority = Authority.new("RUTER") {}
            }

            commit() // needs a roundtrip in db to be available for DSL

            val result = runBlocking {
                findAuthorities("RUTER")
            }

            result shouldHaveSize 1
            result.first().name shouldBe "RUTER"
        }
    }
})
