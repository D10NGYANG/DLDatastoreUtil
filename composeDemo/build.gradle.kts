import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            dependencies {
                // kotlin-serialization
                implementation(libs.kotlinx.serialization.json)
                // 协程
                implementation(libs.kotlinx.coroutines)

                implementation(project(":DLDatastoreUtil"))
            }
        }
        androidMain.dependencies {
            // 协程
            implementation(libs.kotlinx.coroutines.android)
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":DLDatastoreUtil-Processor"))
}

android {
    namespace = "com.d10ng.datastore.compose.demo"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
