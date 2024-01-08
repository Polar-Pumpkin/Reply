plugins {
    val kotlinVersion = "1.8.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.16.0"
}

group = "me.parrot.mirai"
version = "2.1.0"

mirai {
    jvmTarget = JavaVersion.VERSION_1_8
}

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    val exposedVersion = "0.44.0"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-json:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")

    implementation("org.apache.commons:commons-lang3:3.13.0")
    implementation("com.google.guava:guava:32.1.3-jre")
    implementation("com.h2database:h2:2.2.224")}

tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs += "-Xcontext-receivers"
        }
    }
}
