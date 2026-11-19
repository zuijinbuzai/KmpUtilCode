plugins {
    kotlin("multiplatform") version "2.2.20" apply false
    kotlin("android") version "2.2.20" apply false
    kotlin("jvm") version "2.2.20" apply false
    kotlin("plugin.serialization") version "2.2.20" apply false
    id("com.android.library") version "8.11.1" apply false
    id("com.android.application") version "8.11.1" apply false
}
allprojects {
    group = "io.github.zuijinbuzai"
    version = "0.1.0-SNAPSHOT"
}
tasks.register("verify") {
    group = "verification"
    description = "Run JVM tests, Android build and the executable demo."
    dependsOn(subprojects.filter { it.name.startsWith("utilcode") || it.name == "demo-shared" }
        .map { "${it.path}:jvmTest" })
    dependsOn(":demo-android:assembleDebug", ":demo-android:lintDebug", ":demo-jvm:run")
}
