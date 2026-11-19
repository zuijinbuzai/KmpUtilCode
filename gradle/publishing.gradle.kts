configure<PublishingExtension> {
    publications.withType<MavenPublication>().configureEach {
        pom {
            name.set(project.name)
            description.set("Kotlin Multiplatform utilities for Android, iOS and JVM")
            url.set("https://github.com/zuijinbuzai/AppUtilCode")
            licenses { license { name.set("Apache License, Version 2.0"); url.set("https://www.apache.org/licenses/LICENSE-2.0.txt") } }
            developers { developer { id.set("zuijinbuzai"); name.set("zuijinbuzai") } }
            scm { url.set("https://github.com/zuijinbuzai/AppUtilCode"); connection.set("scm:git:https://github.com/zuijinbuzai/AppUtilCode.git") }
        }
    }
}
