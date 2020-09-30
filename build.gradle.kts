plugins {
    kotlin("jvm") version "1.4.10"
}
group = "dev.wnuke"
version = "0.0.1"

repositories {
    mavenCentral()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}