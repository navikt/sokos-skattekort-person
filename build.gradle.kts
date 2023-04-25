import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"

    application
}

group = "no.nav.sokos"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

val ktorVersion = "2.3.0"
val logbackVersion = "1.4.7"
val logstashVersion = "7.3"
val jacksonVersion = "2.14.2"
val prometheusVersion = "1.10.6"
val kotlinLoggingVersion = "3.0.5"
val janionVersion = "3.1.9"
val natpryceVersion = "1.6.10.0"
val kotestVersion = "5.6.1"
val mockkVersion = "1.13.5"
val restAssuredVersion = "5.3.0"
val swaggerRequestValidatorVersion = "2.34.1"
val mockOAuth2ServerVersion = "0.5.8"
val ojdbc10 = "19.18.0.0"

dependencies {

    // Ktor server
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-id-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-server-swagger:$ktorVersion")

    // Ktor client
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-apache-jvm:$ktorVersion")

    implementation("io.ktor:ktor-serialization-jackson-jvm:$ktorVersion")

    // Security
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktorVersion")

    // Jackson
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")

    // Monitorering
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm:$ktorVersion")
    implementation("io.micrometer:micrometer-registry-prometheus:$prometheusVersion")

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
    runtimeOnly("org.codehaus.janino:janino:$janionVersion")
    runtimeOnly("ch.qos.logback:logback-classic:$logbackVersion")
    runtimeOnly("net.logstash.logback:logstash-logback-encoder:$logstashVersion")

    // Config
    implementation("com.natpryce:konfig:$natpryceVersion")

    // Database
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.oracle.database.jdbc:ojdbc10:$ojdbc10")

    // Test
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
    testImplementation("com.atlassian.oai:swagger-request-validator-restassured:$swaggerRequestValidatorVersion")
    testImplementation("no.nav.security:mock-oauth2-server:$mockOAuth2ServerVersion")

}

application {
    mainClass.set("no.nav.sokos.skattekort.person.ApplicationKt")
}

sourceSets {
    main {
        java {
            srcDirs("$buildDir/generated/src/main/kotlin")
        }
    }
}

tasks {

    withType<KotlinCompile>().configureEach {
        compilerOptions.jvmTarget.set(JVM_17)
    }

    withType<ShadowJar>().configureEach {
        enabled = true
        archiveFileName.set("app.jar")
        manifest {
            attributes["Main-Class"] = "no.nav.sokos.skattekort.person.ApplicationKt"
        }
    }

    ("jar") {
        enabled = false
    }

    withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging {
            exceptionFormat = FULL
            events("passed", "skipped", "failed")
        }


        // For å øke hastigheten på build kan vi benytte disse metodene
        maxParallelForks = Runtime.getRuntime().availableProcessors() / 2
        reports.forEach { report -> report.required.value(false) }
    }
}