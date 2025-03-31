import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    // Add Netty DNS resolver for macOS conditionally
    val osName = System.getProperty("os.name")
    if (osName != null && osName.lowercase().contains("mac")) {
        implementation("io.netty:netty-resolver-dns-native-macos:4.1.107.Final:osx-x86_64")
        implementation("io.netty:netty-resolver-dns-native-macos:4.1.107.Final:osx-aarch_64")
    }
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.11.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

testing {
    suites {
        // Configure the default test suite
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter() // Use JUnit Jupiter 5
            targets.all {
                testTask.configure {
                    jvmArgs("-Xshare:off") // Disable CDS to suppress sharing warning
                }
            }
        }
    }
}
