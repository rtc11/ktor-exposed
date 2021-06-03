package database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.*
import io.ktor.config.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource

fun Application.database() {
    Postgres(environment.config)
//    H2()
}

class Postgres(private val config: ApplicationConfig) {
    private val datasource: DataSource = HikariConfig().apply {
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = config.property("database.url").getString()
        username = config.property("database.user").getString()
        password = config.property("database.password").getString()
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }.let(::HikariDataSource)

    init {
        Database.connect(datasource)
        Flyway.configure().dataSource(datasource).load().migrate()
    }
}

internal class H2 {
    private val url = "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;"
    private val driver = "org.h2.Driver"
    private val user = "root"
    private val pass = ""
    private val migrations = "filesystem:testres"

    init {
        Database.connect(url, driver, user, pass)
        Flyway.configure().dataSource(url, user, pass).locations(migrations).load().migrate()!!
    }
}
