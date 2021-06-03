package route

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json

fun Application.routes() {
    installKotlinX()
    install(DefaultHeaders)

    routing {
        get("/") {
            call.respondText("Hello")
        }

        authorities()
        municipalities()
        stops()
    }
}

private fun Application.installKotlinX() {
    install(ContentNegotiation) {
        json(
            Json { prettyPrint = true }
        )
    }
}