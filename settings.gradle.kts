pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}
rootProject.name = "Zanjir"
include(":app")

include(":gereh-core")
project(":gereh-core").projectDir = file("vendor/gereh/mlm-chat/android/core")
