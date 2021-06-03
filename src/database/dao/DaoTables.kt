package database.dao

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IdTable

object Authorities : StringIdTable("authority", "name")
class Authority(id: EntityID<String>) : StringEntity(id) {
    companion object : StringEntityClass<Authority>(Authorities)
}

object AuthorityIds : StringIdTable("authority_id") {
    val name = reference("authority_name", Authorities)
}

class AuthorityId(id: EntityID<String>) : StringEntity(id) {
    companion object : StringEntityClass<AuthorityId>(AuthorityIds)

    var authority by Authority referencedOn AuthorityIds.name
}

object Lines : StringIdTable("line") {
    val name = text("name")
    val transportSubmode = text("transport_sub_mode")
    val authorityId = reference("authority_id_id", AuthorityIds)
}

class Line(id: EntityID<String>) : StringEntity(id) {
    companion object : StringEntityClass<Line>(Lines)

    var name by Lines.name
    var transportSubmode by Lines.transportSubmode
    var authorityId by AuthorityId referencedOn Lines.authorityId
}

////////////////////////////////////////////////
// EXTEND EXPOSED WITH VARCHAR(255) ID ENTITY
////////////////////////////////////////////////

abstract class StringEntity(id: EntityID<String>) : Entity<String>(id)

abstract class StringEntityClass<out E : StringEntity>(
    table: IdTable<String>,
    entityType: Class<E>? = null,
) : EntityClass<String, E>(table, entityType)

open class StringIdTable(name: String = "", columnName: String = "id") : IdTable<String>(name) {
    override val id = varchar(columnName, 255).primaryKey().clientDefault { "unknown" }.entityId()
}
