import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.select
import database.dsl.AuthorityIds
import database.dsl.deleteAuthority
import database.dsl.findAllAuthorities
import database.dsl.saveAuthority
import route.Authority

internal class DslQueryTest : StringSpec({
    val h2 = TestDb.h2()

    "save authority with two ids exists after" {
        h2.withStatement {
            runBlocking {
                saveAuthority(Authority("test", listOf("1", "2")))
            }
            val authorityMap = AuthorityIds
                .select { exists(AuthorityIds.select { AuthorityIds.name eq "test" }) }
                .associate { it[AuthorityIds.id] to it[AuthorityIds.name] }

            authorityMap.count() shouldBe 2
            authorityMap["1"] shouldBe "test"
            authorityMap["2"] shouldBe "test"
        }
    }

    "get all authorities after saving one" {
        h2.withStatement {
            val authorities = runBlocking {
                saveAuthority(Authority("test", listOf("1", "2")))
                findAllAuthorities()
            }
            authorities.size shouldBe 1
            authorities.first().name shouldBe "test"
            authorities.first().ids shouldContainExactly listOf("1", "2")
        }
    }

    "delete authority" {
        h2.withStatement {
            val res = runBlocking {
                saveAuthority(Authority("test", listOf("1")))
                deleteAuthority("test")
            }
            res shouldBe 1
        }
    }
})
