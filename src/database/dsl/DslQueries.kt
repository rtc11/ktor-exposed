package database.dsl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import route.Authority
import route.Municipality
import route.Stop

/**
 *  Run transaction in a thread pool optimized for IO heavy operations
 */
suspend fun <T> asyncTransaction(block: () -> T): T = withContext(Dispatchers.IO) {
    transaction {
        addLogger(StdOutSqlLogger)
        block()
    }
}

// showcase: for readability
fun ResultRow.toMunicipality() = Municipality(
    id = this[Municipalities.id],
    name = this[Municipalities.name]
)

/**
 * SELECT *
 * TYPE AND NULLSAFE DSL
 */
suspend fun getAllMunicipalities() = asyncTransaction {
    Municipalities
        .selectAll()
        .map(ResultRow::toMunicipality)
}

/**
 * SELECT WHERE
 */
suspend fun getMunicipalitiesByName(name: String) = asyncTransaction {
    Municipalities
        .select { Municipalities.name eq name }
        .map(ResultRow::toMunicipality)
}

/**
 * INSERT
 */
suspend fun saveAuthority(authority: Authority) = asyncTransaction {
    Authorities.insert {
        it[name] = authority.name
    }
    AuthorityIds.batchInsert(authority.ids) { id ->
        this[AuthorityIds.id] = id
        this[AuthorityIds.name] = authority.name
    }
}

/**
 * UPDATE
 */
suspend fun replaceAuthority(name: String, authority: Authority) = asyncTransaction {
    authority.ids.forEach { id ->
        AuthorityIds.update({ AuthorityIds.name eq name }) {
            it[AuthorityIds.name] = authority.name
            it[AuthorityIds.id] = id
        }
    }
}

/**
 * DELETE WHERE
 */
suspend fun deleteAuthority(name: String) = asyncTransaction {
    AuthorityIds.deleteWhere { AuthorityIds.name eq name }
}

/**
 * SELECT FROM INNER JOIN
 */
suspend fun getStopById(stopId: String) = asyncTransaction {
    (Stops innerJoin Municipalities).slice(
        Stops.id,
        Stops.name,
        Municipalities.id,
        Municipalities.name,
    ).select { Stops.id eq stopId }
        .map {
            Stop(
                id = it[Stops.id],
                name = it[Stops.name],
                municipality = Municipality(
                    id = it[Municipalities.id],
                    name = it[Municipalities.name]
                )
            )
        }
}

/**
 * GROUP BY
 */
suspend fun findAllAuthorities(): List<Authority> = asyncTransaction {
    AuthorityIds
        .selectAll()
        .groupBy(AuthorityIds.name, AuthorityIds.id)
        .map {
            Authority(
                name = it[AuthorityIds.name],
                ids = listOf(it[AuthorityIds.id])
            )
        }
        .groupBy(Authority::name)
        .map { (_, authoritiesWithSameName) ->
            authoritiesWithSameName.reduce { acc, authority ->
                acc.copy(ids = acc.ids + authority.ids)
            }
        }
}

/**
 * GROUP BY WHERE
 */
suspend fun findAuthorities(name: String) = asyncTransaction {
    AuthorityIds
        .select { AuthorityIds.name eq name }
        .groupBy(AuthorityIds.name, AuthorityIds.id)
        .map {
            Authority(
                name = it[AuthorityIds.name],
                ids = listOf(it[AuthorityIds.id])
            )
        }
        .groupBy(Authority::name)
        .map { (_, authoritiesWithSameName) ->
            authoritiesWithSameName.reduce { acc, authority ->
                acc.copy(ids = acc.ids + authority.ids)
            }
        }
}
