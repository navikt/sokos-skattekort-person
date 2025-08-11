import kotlinx.kover.gradle.plugin.dsl.tasks.KoverReport

import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.2.0"
    id("com.gradleup.shadow") version "9.0.1"
    id("com.expediagroup.graphql") version "8.8.1"
    id("org.jlleitschuh.gradle.ktlint") version "13.0.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
}

group = "no.nav.sokos"

repositories {
    mavenCentral()
}

val ktorVersion = "3.2.3"
val graphqlClientVersion = "8.8.1"
val jacksonVersion = "2.19.2"
val konfigVersion = "1.6.10.0"
val oracleJDBC11 = "23.9.0.25.07"
val hikaricpVersion = "7.0.1"
val kotliqueryVersion = "1.9.1"
val kotestVersion = "5.9.1"
val mockkVersion = "1.14.5"
val swaggerRequestValidatorVersion = "2.45.1"
val mockOAuth2ServerVersion = "2.2.1"
val wiremockVersion = "3.13.1"
val janinoVersion = "3.1.12"
val kotlinLoggingVersion = "3.0.5"
val logbackVersion = "1.5.18"
val logstashVersion = "8.1"
val papertrailappVersion = "1.0.0"
val micrometerVersion = "1.15.2"

dependencies {

    // Ktor server
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-server-swagger:$ktorVersion")

    // Ktor client
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-apache-jvm:$ktorVersion")

    // Security
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktorVersion")

    // Serialization
    implementation("io.ktor:ktor-serialization-jackson-jvm:$ktorVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")

    // Monitorering
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm:$ktorVersion")
    implementation("io.micrometer:micrometer-registry-prometheus:$micrometerVersion")

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
    runtimeOnly("org.codehaus.janino:janino:$janinoVersion")
    runtimeOnly("ch.qos.logback:logback-classic:$logbackVersion")
    runtimeOnly("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    runtimeOnly("com.papertrailapp:logback-syslog4j:$papertrailappVersion")

    // Config
    implementation("com.natpryce:konfig:$konfigVersion")

    // Database
    implementation("com.zaxxer:HikariCP:$hikaricpVersion")
    implementation("com.oracle.database.jdbc:ojdbc11:$oracleJDBC11")
    implementation("com.github.seratch:kotliquery:$kotliqueryVersion")

    // Test
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("no.nav.security:mock-oauth2-server:$mockOAuth2ServerVersion")
    testImplementation("com.atlassian.oai:swagger-request-validator-restassured:$swaggerRequestValidatorVersion")
    testImplementation("org.wiremock:wiremock:$wiremockVersion")

    // GraphQL
    implementation("com.expediagroup:graphql-kotlin-ktor-client:$graphqlClientVersion") {
        exclude("com.expediagroup:graphql-kotlin-client-serialization")
    }
    implementation("com.expediagroup:graphql-kotlin-client-jackson:$graphqlClientVersion")
}

// Vulnerability fix because of id("org.jlleitschuh.gradle.ktlint") uses ch.qos.logback:logback-classic:1.3.5
configurations.ktlint {
    resolutionStrategy.force("ch.qos.logback:logback-classic:$logbackVersion")
}

sourceSets {
    main {
        java {
            srcDirs("${layout.buildDirectory.get()}/generated/src/main/kotlin")
        }
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {

    named("runKtlintCheckOverMainSourceSet").configure {
        dependsOn("graphqlGenerateClient")
    }

    named("runKtlintFormatOverMainSourceSet") {
        dependsOn("graphqlGenerateClient")
    }
    withType<KotlinCompile>().configureEach {
        dependsOn("ktlintFormat")
        dependsOn("graphqlGenerateClient")
    }

    withType<ShadowJar>().configureEach {
        enabled = true
        archiveFileName.set("app.jar")
        manifest {
            attributes["Main-Class"] = "no.nav.sokos.skattekort.person.ApplicationKt"
        }
        finalizedBy(koverHtmlReport)
    }

    withType<KoverReport>().configureEach {
        kover {
            reports {
                filters {
                    excludes {
                        // exclusion rules - classes to exclude from report
                        classes("no.nav.pdl.*")
                    }
                }
            }
        }
    }

    ("jar") {
        enabled = false
    }

    withType<Test>().configureEach {
        useJUnitPlatform()

        testLogging {
            showExceptions = true
            showStackTraces = true
            exceptionFormat = FULL
            events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        }

        reports.forEach { report -> report.required.value(false) }
    }

    withType<Wrapper> {
        gradleVersion = "8.14"
    }

    withType<GraphQLGenerateClientTask>().configureEach {
        packageName = "no.nav.sokos.skattekort.person.pdl"
        schemaFile = file("$projectDir/src/main/resources/pdl/schema.graphql")
        queryFileDirectory.set(file("$projectDir/src/main/resources/pdl"))
        serializer = GraphQLSerializer.JACKSON
    }

    ("build") {
        dependsOn("copyPreCommitHook")
    }

    register<Copy>("copyPreCommitHook") {
        from(".scripts/pre-commit")
        into(".git/hooks")
        filePermissions {
            user {
                execute = true
            }
        }
        doFirst {
            println("Installing git hooks...")
        }
        doLast {
            println("Git hooks installed successfully.")
        }
        description = "Copy pre-commit hook to .git/hooks"
        group = "git hooks"
        outputs.upToDateWhen { false }
    }
}
