plugins { id("com.android.application"); kotlin("android") }
android {
    namespace = "io.github.zuijinbuzai.kmputilcode.demo.android"
    compileSdk = 35
    defaultConfig {
        applicationId = "io.github.zuijinbuzai.kmputilcode.demo"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}
kotlin { jvmToolchain(21)
    compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
dependencies { implementation(project(":demo-shared")); implementation(libs.coroutines.android) }
