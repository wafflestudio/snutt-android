import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.firebase.appdistribution)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.compose.compiler)
}

ktlint {
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
    }
    filter {
        exclude("**/generated/**")
        include("**/java/**")
    }
}

val versionProps =
    Properties().apply {
        load(FileInputStream(File(rootProject.rootDir, "version.properties")))
    }

android {
    namespace = "com.wafflestudio.snutt2"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.wafflestudio.snutt2"
        minSdk = 24
        targetSdk = 36
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    signingConfigs {
        create("release") {
            keyAlias = System.getenv("SIGNING_KEY_ALIAS")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
            storeFile = file("keystore/android.jks")
            storePassword = System.getenv("SIGNING_STORE_PASSWORD")
        }
    }

    buildTypes {
        getByName("debug") {
            isDefault = true
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    flavorDimensions.add("mode")

    productFlavors {
        create("staging") {
            isDefault = true
            applicationIdSuffix = ".staging"

            val propertyVersionName = versionProps.getProperty("snuttVersionName")
            versionCode = SemVer.sementicVersionToSerializedCode(propertyVersionName).toInt()
            versionName = propertyVersionName
            configure<com.google.firebase.appdistribution.gradle.AppDistributionExtension> {
                artifactType = "APK"
                testers = "urban"
                serviceCredentialsFile = "gcp-service-account-staging.json"
                appId = System.getenv("FIREBASE_APP_ID")
            }
        }

        create("live") {
            applicationIdSuffix = ".live"

            val propertyVersionName = versionProps.getProperty("snuttVersionName")
            versionCode = SemVer.sementicVersionToSerializedCode(propertyVersionName).toInt()
            versionName = propertyVersionName
            configure<com.google.firebase.appdistribution.gradle.AppDistributionExtension> {
                artifactType = "AAB"
                serviceCredentialsFile = "gcp-service-account-live.json"
                appId = System.getenv("FIREBASE_APP_ID")
            }
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    // Testing
    testImplementation(libs.junit)

    // Android Core
    implementation(libs.material)
    implementation(libs.bundles.kotlin.core)

    // Networking
    implementation(libs.bundles.moshi)
    implementation(libs.bundles.retrofit)

    // Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Navigation
    implementation(libs.navigation.compose)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    // Compose
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.bundles.compose)
    implementation(libs.compose.material.navigation)
    implementation(libs.paging.compose)
    implementation(libs.bundles.lifecycle)
    implementation(libs.hilt.navigation.compose)

    // UI & Misc
    implementation(libs.androidx.core.ktx)
    implementation(libs.facebook.login)
    implementation(libs.timber)
    implementation(libs.androidx.core.splashscreen)

    // Image Loading
    implementation(libs.coil.compose)

    // JSON
    implementation(libs.gson)

    // Maps
    implementation(libs.naver.map)
    implementation(libs.naver.map.compose)

    // Authentication
    implementation(libs.google.auth)
    implementation(libs.google.id)

    // Kakao SDK
    implementation(libs.bundles.kakao)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Haze
    implementation(libs.haze)

    // Desugaring
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
