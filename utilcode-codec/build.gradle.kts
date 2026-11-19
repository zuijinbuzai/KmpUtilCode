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
        commonMain.dependencies { api(libs.okio) }
        commonTest.dependencies { implementation(kotlin("test"));  }

    }
}
android {
    namespace = "io.github.zuijinbuzai.kmputilcode.codec"
    compileSdk = 35
    defaultConfig { minSdk = 23 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
apply(from = rootProject.file("gradle/publishing.gradle.kts"))
