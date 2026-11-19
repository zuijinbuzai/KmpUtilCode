plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
}
kotlin {
    jvmToolchain(21)
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) }
    }
    jvm { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework { baseName = "DemoKit"; isStatic = true; binaryOption("objcExportSuspendFunctionLaunchThreadRestriction", "none") }
    }
    sourceSets {
        commonMain.dependencies { api(project(":utilcode")) }
        commonTest.dependencies { implementation(kotlin("test")); implementation(libs.coroutines.test);  }

    }
}
android {
    namespace = "io.github.zuijinbuzai.kmputilcode.demo"
    compileSdk = 35
    defaultConfig { minSdk = 23 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
