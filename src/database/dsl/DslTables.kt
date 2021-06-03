package database.dsl

import org.jetbrains.exposed.sql.Table

/**
 * Schema mapping
 */
object AuthorityIds : Table("authority_id") {
    val id = varchar("id", 255).primaryKey()
    val name = varchar("authority_name", 255) references Authorities.name
}

object Authorities : Table("authority") {
    val name = varchar("name", 255).primaryKey()
}

object Municipalities : Table("municipality") {
    val id = varchar("id", 255).primaryKey()
    val name = varchar("name", 255)
}

object Stops : Table("stop_place") {
    val id = varchar("id", 255).primaryKey()
    val name = varchar("name", 255)
    val municipalityId = varchar("municipality_id", 255) references Municipalities.id
}
