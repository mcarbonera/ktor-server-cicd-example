plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
    id("org.flywaydb.flyway") version "11.8.0"
}

group = "com.evennt"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.ktor.core)
    implementation(libs.bundles.security)
    implementation(libs.bundles.api.docs)
    implementation(libs.bundles.exposed)
    implementation(libs.bundles.database)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.utils)

    testImplementation(enforcedPlatform(libs.junit.bom))
    testImplementation(libs.bundles.testing)
}

tasks.test {
    useJUnitPlatform()
}
