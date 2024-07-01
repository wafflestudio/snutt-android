pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven("https://naver.jfrog.io/artifactory/maven/")
    }
}
rootProject.name = "snutt"

include(":app")
include(":core:data")
include(":core:database")
include(":core:network")

