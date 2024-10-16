import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.gms.google-services")
    id("org.jlleitschuh.gradle.ktlint-idea")
    id("org.jlleitschuh.gradle.ktlint")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    id("com.google.firebase.appdistribution")
    id("com.google.firebase.crashlytics")
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
    // See https://github.com/pinterest/ktlint/issues/527
    disabledRules.addAll("import-ordering", "no-wildcard-imports", "package-name", "argument-list-wrapping")
}

val versionProps = Properties().apply {
    load(FileInputStream(File(rootProject.rootDir, "version.properties")))
}

android {
    namespace = "com.wafflestudio.snutt2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.wafflestudio.snutt2"
        minSdk = 24
        targetSdk = 34
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
            proguardFiles(getDefaultProguardFile("proguard-android.txt"))
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
            }
        }
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Deps.Version.Kotlin}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Deps.Version.Kotlin}")

    // Moshi
    implementation("com.squareup.moshi:moshi:${Deps.Version.Moshi}")
    implementation("com.squareup.moshi:moshi-kotlin:${Deps.Version.Moshi}")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:${Deps.Version.Retrofit}")
    implementation("com.squareup.retrofit2:adapter-rxjava3:${Deps.Version.Retrofit}")
    implementation("com.squareup.retrofit2:converter-moshi:${Deps.Version.Retrofit}")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.2")

    // RxJava
    implementation("io.reactivex.rxjava3:rxjava:${Deps.Version.RxJava}")
    implementation("io.reactivex.rxjava3:rxkotlin:${Deps.Version.RxKotlin}")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:${Deps.Version.Hilt}")
    kapt("com.google.dagger:hilt-android-compiler:${Deps.Version.Hilt}")

    // AAC Navigation
    implementation("androidx.navigation:navigation-compose:${Deps.Version.Navigation}")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:28.1.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    // Paging
    implementation("androidx.paging:paging-runtime-ktx:${Deps.Version.Paging}")

    // Compose
    implementation("androidx.compose.runtime:runtime:${Deps.Version.Compose}")
    implementation("androidx.compose.ui:ui:${Deps.Version.Compose}")
    implementation("androidx.compose.ui:ui-tooling:${Deps.Version.Compose}")
    implementation("androidx.compose.material:material:${Deps.Version.Compose}")
    implementation("androidx.compose.foundation:foundation:${Deps.Version.ComposeFoundation}")
    implementation("androidx.compose.foundation:foundation-layout:${Deps.Version.ComposeFoundation}")
    implementation("androidx.compose.runtime:runtime-livedata:${Deps.Version.Compose}")
    implementation("androidx.paging:paging-compose:${Deps.Version.PagingCompose}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${Deps.Version.Lifecycle}")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:${Deps.Version.Lifecycle}")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    // misc
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("com.google.accompanist:accompanist-pager:0.20.3")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.20.3")
    implementation("com.facebook.android:facebook-login:15.0.1")
    implementation("de.psdev.licensesdialog:licensesdialog:2.1.0")
    implementation("com.github.skydoves:colorpickerview:2.2.3")
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.google.accompanist:accompanist-navigation-material:0.32.0")

    // coil
    implementation("io.coil-kt:coil-compose:2.1.0")

    // GSON
    implementation("com.google.code.gson:gson:2.10.1")

    // RN
    implementation("com.facebook.react:react-android:0.72.3")
    implementation("com.facebook.react:hermes-android:0.72.3")
    implementation(fileTree(mapOf("dir" to "$rootDir/libs", "include" to listOf("*.aar"))))

    // naver map
    implementation("com.naver.maps:map-sdk:3.17.0")
    implementation("io.github.fornewid:naver-map-compose:1.4.1")

    // google login
    implementation("com.google.android.gms:play-services-auth:21.1.1")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")

    // Kakao SDK
    implementation("com.kakao.sdk:v2-share:${Deps.Version.KakaoSDK}")
    implementation("com.kakao.sdk:v2-user:${Deps.Version.KakaoSDK}")
}
