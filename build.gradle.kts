import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.5.0"

    id("org.springframework.boot") version "2.4.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.revolut.jooq-docker") version "0.3.5"

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
}

group = "se.jh.jooqissue"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

val generatedPath = "$projectDir/src/main/generated"

buildscript {
    configurations.classpath {
        resolutionStrategy {
            setForcedModules("org.jooq:jooq-codegen:3.14.11")
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.flywaydb:flyway-core:7.7.1")
    implementation("org.jooq:jooq:3.14.11")
    implementation("org.jooq:jooq-kotlin:3.14.11")

    runtimeOnly("org.postgresql:postgresql:42.2.5")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.4.2")
    testImplementation("org.testcontainers:junit-jupiter:1.15.2")
    testImplementation("org.testcontainers:postgresql:1.15.2")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter:5.4.2")

    jdbc("org.postgresql:postgresql:42.2.5")
}

val SourceSet.kotlin: SourceDirectorySet
    get() = this.withConvention(KotlinSourceSet::class) { kotlin }
sourceSets["main"].kotlin.srcDir(generatedPath)

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks {
    generateJooqClasses {
        basePackageName = "se.jh.jooqissue.db"
        outputDirectory.set(file(generatedPath))
        customizeGenerator {
            name = "org.jooq.codegen.KotlinGenerator"
            database.excludes = "flyway_schema_history"
        }
    }
}
