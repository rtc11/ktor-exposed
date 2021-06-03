package route

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import database.dsl.getAllMunicipalities
import database.dsl.getMunicipalitiesByName

fun Route.municipalities() {
    get("/municipalities/{name?}") {
        when (val name = call.parameters["name"]) {
            null -> call.respond(getAllMunicipalities())
            else -> call.respond(getMunicipalitiesByName(name))
        }
    }
}
