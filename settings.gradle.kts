pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}
rootProject.name = "KmpUtilCode"
include(":utilcode-core", ":utilcode-codec", ":utilcode-time", ":utilcode-coroutines",
    ":utilcode-serialization", ":utilcode-storage", ":utilcode-file", ":utilcode-network",
    ":utilcode-platform", ":utilcode", ":demo-shared", ":demo-jvm", ":demo-android")
