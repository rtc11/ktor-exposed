package route

import io.ktor.application.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.response.*
import io.ktor.routing.*
import database.dsl.getStopById

fun Route.stops() {
    get("/stops/{id?}") {
        when (val stopId = call.parameters["id"]) {
            null -> call.respondText("Id missing", status = BadRequest)
            else -> call.respond(getStopById(stopId))
        }
    }
}
