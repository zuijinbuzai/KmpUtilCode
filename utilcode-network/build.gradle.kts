plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
}
kotlin {
    jvmToolchain(21)
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) }
    }
    jvm { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
    iosArm64()
    iosSimulatorArm64()
    sourceSets {
        commonMain.dependencies { api(libs.ktor.core)
                implementation(libs.ktor.content.negotiation)
                implementation(libs.ktor.json)
                api(project(":utilcode-serialization")) }
        commonTest.dependencies { implementation(kotlin("test")); implementation(libs.coroutines.test); implementation(libs.ktor.mock) }
        androidMain.dependencies { implementation(libs.ktor.okhttp) }
        jvmMain.dependencies { implementation(libs.ktor.okhttp) }
        iosMain.dependencies { implementation(libs.ktor.darwin) }
    }
}
android {
    namespace = "io.github.zuijinbuzai.kmputilcode.network"
    compileSdk = 35
    defaultConfig { minSdk = 23 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
apply(from = rootProject.file("gradle/publishing.gradle.kts"))
