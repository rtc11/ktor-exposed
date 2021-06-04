[![Gradle Build](https://github.com/rtc11/ktor-exposed/actions/workflows/main.yml/badge.svg)](https://github.com/rtc11/ktor-exposed/actions/workflows/main.yml)

# Documentation

- ktor (netty)
- kotlinx.serialization
- postgresql/h2, flyway
- exposed

## Why Exposed?

- Kotlin have a functional nature with first-class immutability.
- Exposed have a functional nature with typesafe SQL with DSL or DAO
- Not as ORMY as Hibernate
- Lightweight
- Starter for Spring Boot ([by jetbrains](https://github.com/JetBrains/Exposed/tree/master/exposed-spring-boot-starter)) to replace Hibernate
- No annotations
- Extendable by design

## DSL
#### Data class
kotlinx is supported, obviously.
```kotlin
@Serializable
data class Authority(val name: String, val ids: List<String>)
```

#### Tables

```kotlin
object Authorities : Table("authority") {
    val name = varchar("name", 255).primaryKey()
}

object AuthorityIds : Table("authority_id") {
    val id = varchar("id", 255).primaryKey()
    val name = varchar("authority_name", 255) references Authorities.name
}
```

#### Queries

```kotlin
AuthorityIds
    .select { AuthorityIds.name eq name }
    .groupBy(AuthorityIds.name, AuthorityIds.id)
    .map {
        Authority(
            name = it[AuthorityIds.name],
            ids = listOf(it[AuthorityIds.id])
        )
    }
```

### Notes on DSL
- You have to do relational mapping by yourself
    - cons: not as easy as with (perfect modelling in) ORM
    - pros: things go wrong with relations in ORM
- You have to think more in SQL on how relational data behaves
    - Aggregate when grouping with e.g mapping and reducing the resultset 
    - Select relevant columns and join where you like
- Make your data classes as it suits you (have SQL performance in mind)
- Similar mindset as with kafka streams dsl

## DAO
If you have relations and want to make it easier to work with references, use DAO. <br>
This is an `active record pattern` implementation for querying the database.

#### Classes
The `var` is neccessary with the `active record pattern`.
```kotlin
class AuthorityId(id: EntityID<String>) : StringEntity(id) {
    companion object : StringEntityClass<AuthorityId>(AuthorityIds)

    var authority by Authority referencedOn AuthorityIds.name
}

class Authority(id: EntityID<String>) : StringEntity(id) {
  companion object : StringEntityClass<Authority>(Authorities)
}
```

#### Tables
StringIdTable is a custom extension of exposed to support varchar(255) ids.
```kotlin
object Authorities : StringIdTable("authority", "name")

object AuthorityIds : StringIdTable("authority_id") {
  val name = reference("authority_name", Authorities)
}
```

#### Queries
Insert with reference
```kotlin
AuthorityId.new("RUT:Authority:RUT") {
  authority = Authority.new("RUTER") {}
}
```

Update a record
```kotlin
val line = Line.new("Line:123") { name = "Helsfyr" } // insert statement
line.name = "Tjuvholmen" // update statement
```

Delete a record
```kotlin
line.delete()
```

#### Extensions
Exposed doesnt support UInt, create it yourself with `field transformation`.
```kotlin
object TableWithUnsignedInteger : IntIdTable() {
    val uint = integer("uint")
}
class EntityWithUInt : IntEntity() {
    var uint: UInt by TableWithUnsignedInteger.uint.transform({ it.toInt() }, { it.toUInt() })
    
    companion object : IntEntityClass<EntityWithUInt>()
}
```

### Notes on DAO
- Not obvious with batching with potential many separate SQL statements
- Makes the use of data classes obsolete
  - work directly with your DAOs 
  - no mapping needed

# Dev mode

Enable hot reload in ktor. (might require -t program args)

```kotlin
ktor {
    development = true

    deployment {
        watch = [classes, resources]
    }
}
```

Build continuously

```bash
./gradlew -t build
``` 

Test continuously

```bash
./gradlew -t test
``` 
