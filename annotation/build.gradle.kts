plugins {
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
}

group = lib_group
version = lib_ver

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    jvmToolchain(8)
}

val bds100MavenUsername: String by project
val bds100MavenPassword: String by project

publishing {
    publications {
        create("release", MavenPublication::class) {
            artifactId = "DLDatastoreUtil-Annotation"
            from(components.getByName("java"))
        }
    }
    repositories {
        maven {
            url = uri("/Users/d10ng/project/kotlin/maven-repo/repository")
        }
        maven {
            credentials {
                username = bds100MavenUsername
                password = bds100MavenPassword
            }
            setUrl("https://nexus.bds100.com/repository/maven-releases/")
        }
    }
}