rootProject.name = "DLDatastoreUtil-Project"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        repositories {
            maven("https://raw.githubusercontent.com/D10NGYANG/maven-repo/main/repository") {
                mavenContent {
                    includeGroupAndSubgroups("com.github.D10NGYANG")
                }
            }
            google()
            mavenCentral()
        }
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include( ":library", ":processor")
project(":library").name = "DLDatastoreUtil"
project(":processor").name = "DLDatastoreUtil-Processor"
include(":androidDemo", ":composeDemo")
