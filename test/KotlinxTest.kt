import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import route.Authority

internal class KotlinxTest : StringSpec({
    val expected = Authority("robin", listOf("1"))

    "authority uses kotlinx.serializer" {
        val encoded = Json.encodeToString(expected)
        val decoded = Json.decodeFromString<Authority>(encoded)

        expected shouldBe decoded
    }
})