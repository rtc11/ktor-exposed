package route

import io.ktor.application.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import database.dsl.findAllAuthorities
import database.dsl.findAuthorities
import database.dsl.saveAuthority

fun Route.authorities() {
    route("/authorities/{name?}") {
        post {
            when (val name = call.parameters["name"]) {
                null -> createAuthority()
                else -> replaceAuthority(name)
            }
        }
        get {
            when (val name = call.parameters["name"]) {
                null -> call.respond(findAllAuthorities())
                else -> call.respond(findAuthorities(name))
            }
        }
        put {
            when (val name = call.parameters["name"]) {
                null -> call.respondText("Missing parameter 'name'", status = BadRequest)
                else -> replaceAuthority(name)
            }
        }
        delete {
            when (val name = call.parameters["name"]) {
                null -> call.respondText("Missing parameter 'name'", status = BadRequest)
                else -> deleteAuthority(name)
            }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.createAuthority() {
    val authority = call.receive<Authority>()
    saveAuthority(authority)
    call.respondText(authority.name, status = Created)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.replaceAuthority(name: String) {
    val authority = call.receive<Authority>()
    database.dsl.replaceAuthority(name, authority)
    call.respondText(name, status = Created)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.deleteAuthority(name: String) {
    database.dsl.deleteAuthority(name)
    call.respondText(name, status = OK)
}
