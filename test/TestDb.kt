import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.transactionManager

data class TestDb(
    val url: String = "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;",
    val driver: String = "org.h2.Driver",
    val user: String = "root",
    val pass: String = "",
    val migrationLocation: String = "filesystem:testres",
    val beforeEach: () -> Unit = {},
    val afterEach: () -> Unit = {},
    var db: Database? = null,
) {
    companion object {
        fun h2(
            beforeEach: () -> Unit = {},
            afterEach: () -> Unit = truncateTables(),
        ): TestDb = TestDb(beforeEach = beforeEach, afterEach = afterEach).apply {
            db = Database.connect(url, driver, user, pass)
            Flyway.configure().dataSource(url, user, pass).locations(migrationLocation).load().migrate()
        }

        private fun truncateTables(): () -> Unit = {
            transaction {
                exec("SET REFERENTIAL_INTEGRITY FALSE")
                exec(" TRUNCATE TABLE authority")
                exec(" TRUNCATE TABLE authority_id")
                exec(" TRUNCATE TABLE line")
                exec("SET REFERENTIAL_INTEGRITY TRUE")
            }
        }
    }
}

fun TestDb.withStatement(statement: Transaction.(TestDb) -> Unit) {
    beforeEach()
    transaction(db.transactionManager.defaultIsolationLevel, 1, db) {
        addLogger(StdOutSqlLogger)
        statement(this@withStatement)
    }
    afterEach()
}

fun TestDb.withTables(vararg tables: Table, statement: Transaction.(TestDb) -> Unit) {
    this.withStatement {
        SchemaUtils.create(*tables)
        try {
            statement(this@withTables)
            commit() // persist data before drop tables
        } finally {
            SchemaUtils.drop(*tables)
            commit()
        }
    }
}
