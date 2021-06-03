import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.10"
}

group = "no.tordly.ktor"
version = "0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
}

dependencies {
    val ktor_version = "1.6.0"

    implementation(kotlin("reflect"))
    implementation("ch.qos.logback:logback-classic:1.2.3")
    testImplementation("io.kotest:kotest-runner-junit5:4.6.0")

    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")

    implementation("org.jetbrains.exposed:exposed:0.17.13")
    implementation("com.zaxxer:HikariCP:2.7.8")
    implementation("org.postgresql:postgresql:42.2.20")
    implementation("org.flywaydb:flyway-core:7.8.2")
    testRuntimeOnly("com.h2database:h2:1.4.200")
}

kotlin.sourceSets["main"].kotlin.srcDir("src")
kotlin.sourceSets["test"].kotlin.srcDir("test")
sourceSets["main"].resources.srcDir("res")
sourceSets["test"].resources.srcDir("testres")

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "15"
    }
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            showCauses = true
            showExceptions = true
            events("passed", "failed")
        }
    }
}