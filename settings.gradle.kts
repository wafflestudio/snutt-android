pluginManagement {
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
        maven("https://repository.map.naver.com/archive/maven")
        maven("https://devrepo.kakao.com/nexus/content/groups/public/")
    }
}

rootProject.name = "snutt2"
include(":app")

plugins {
    id("com.github.burrunan.s3-build-cache") version "1.9.3"
}

val isGithubAction = true //System.getenv("GITHUB_ACTIONS") == "true"

buildCache {
    // GITHUB ACTION 에서 수행된 경우가 아니면 Local Cache 사용
    local {
        isEnabled = isGithubAction.not()
    }

    // GITHUB ACTION 에서 수행되었으면 Remote Cache(S3) 사용
    if (isGithubAction) {
        remote<com.github.burrunan.s3cache.AwsS3BuildCache> {
            region = "ap-northeast-2"
            bucket = "snutt-android-build-cache"
            prefix = "gradle/build/snutt-android/"
            isPush = true
            lookupDefaultAwsCredentials = true
        }
    }
}
