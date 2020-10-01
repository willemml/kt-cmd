plugins {
    kotlin("jvm") version "1.4.10"
    `maven-publish`
}
group = "dev.wnuke"
version = "1.2.2"

repositories {
    mavenCentral()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group as String
            artifactId = rootProject.name
            version = version as String

            from(components["java"])
        }
    }
}
