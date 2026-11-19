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
        commonMain.dependencies {
            api(project(":utilcode-core"))
            api(project(":utilcode-codec"))
            api(project(":utilcode-time"))
            api(project(":utilcode-coroutines"))
            api(project(":utilcode-serialization"))
            api(project(":utilcode-storage"))
            api(project(":utilcode-file"))
            api(project(":utilcode-network"))
            api(project(":utilcode-platform"))
        }
        commonTest.dependencies { implementation(kotlin("test"));  }

    }
}
android {
    namespace = "io.github.zuijinbuzai.kmputilcode.all"
    compileSdk = 35
    defaultConfig { minSdk = 23 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
apply(from = rootProject.file("gradle/publishing.gradle.kts"))
