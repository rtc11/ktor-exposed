import io.ktor.application.*
import database.database
import route.routes

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    routes()
    database()
}
