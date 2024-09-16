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
        maven("https://repository.map.naver.com/archive/maven")
        maven("https://devrepo.kakao.com/nexus/content/groups/public/")
    }
}
rootProject.name = "snutt"

include(":app")
include(":core:database")
include(":core:network")
include(":core:model")
include(":core:data")
include(":core:qualifiers")
include(":core:ui")
