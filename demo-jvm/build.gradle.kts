plugins { kotlin("jvm"); application }
kotlin { jvmToolchain(21)
    compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
dependencies { implementation(project(":demo-shared")) }
application { mainClass.set("io.github.zuijinbuzai.kmputilcode.demo.MainKt") }

java { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
