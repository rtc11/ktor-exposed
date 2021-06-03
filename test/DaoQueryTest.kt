import database.dao.Authority
import database.dao.AuthorityId
import database.dao.Line
import database.dao.Lines
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jetbrains.exposed.dao.load
import java.util.UUID

internal class DaoQueryTest : FunSpec({
    val h2 = TestDb.h2()

    test("Authority can be inserted") {
        h2.withStatement {
            Authority.new("RUTER") {}

            val result = Authority.all().first()
            result.id.value shouldBe "RUTER"
        }
    }

    test("many-to-one reference can be inserted") {
        h2.withStatement {
            Line.new("Line:21") {
                name = "Helsfyr"
                transportSubmode = "localbus"
                authorityId = AuthorityId.new("RUT:Authority:RUT") {
                    authority = Authority.new("RUTER") {}
                }
            }

            val result = Line.find { Lines.id eq "Line:21" }.limit(1).first()
            result.id.value shouldBe "Line:21"
            result.name shouldBe "Helsfyr"
            result.authorityId.id.value shouldBe "RUT:Authority:RUT"
            result.authorityId.authority.id.value shouldBe "RUTER"
        }
    }

    test("Line can be updated") {
        h2.withStatement {
            val line = Line.new("Line:21") {
                name = "Helsfyr"
                transportSubmode = "localbus"
                authorityId = AuthorityId.new("RUT:Authority:RUT") {
                    authority = Authority.new("RUTER") {}
                }
            }
            commit() // without commit insert and update is merged within the transaction - will still work though.
            line.name = "Tjuvholmen"

            val updated = Line.find { Lines.id eq "Line:21" }
            updated.first().name shouldBe "Tjuvholmen"
        }
    }

    test("many-to-one authority join authorityId") {
        h2.withStatement {
            AuthorityId.new("RUT:Authority:RUT") {
                authority = Authority.new("RUTER") {}
            }

            val authId = AuthorityId.all().first()
            authId.id.value shouldBe "RUT:Authority:RUT"
            authId.authority.id.value shouldBe "RUTER"
        }
    }

    test("delete an authority") {
        h2.withStatement {
            val authority = Authority.new("1") {}
            commit()
            authority.delete()
        }
    }

    test("eager loading") {
        h2.withStatement {
            AuthorityId.new("RUT:Authority:RUT") {
                authority = Authority.new("RUTER") {}
            }

            val aId = AuthorityId.all().first().load(AuthorityId::authority)
            aId shouldNotBe null
        }
    }

    test("10K statements") {
        h2.withStatement {
            for (i in 1..10000) {
                Authority.new("$i") {}
            }
        }
    }
})
