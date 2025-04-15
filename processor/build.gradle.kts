plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("maven-publish")
}

group = libs.versions.lib.group.get()
version = libs.versions.lib.ver.get()

kotlin {
    jvmToolchain(8)
    jvm {
        withJava()
    }
    sourceSets {
        jvmMain {
            dependencies {
                // 反射
                implementation(kotlin("reflect"))
                // ksp
                implementation(libs.symbol.processing.api)
            }
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
        }
    }
}

val bds100MavenUsername: String by project
val bds100MavenPassword: String by project

publishing {
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