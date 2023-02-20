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
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
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
    disabledRules.addAll("import-ordering", "no-wildcard-imports")
}

val versionProps = Properties().apply {
    load(FileInputStream(File(rootProject.rootDir, "version.properties")))
}

android {
    compileSdk = 33

    repositories {
        mavenCentral()
    }

    defaultConfig {
        applicationId = "com.wafflestudio.snutt2"
        minSdk = 24
        targetSdk = 33
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"))
        }

        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    viewBinding {
        isEnabled = true
    }

    flavorDimensions.add("mode")

    productFlavors {
        create("staging") {
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
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Deps.Version.Compose
    }
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions.freeCompilerArgs += listOf("-Xuse-experimental=androidx.compose.ui.ExperimentalCompose", "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi")
}

dependencies {
    testImplementation("junit:junit:4.13.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")

    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Deps.Version.Kotlin}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Deps.Version.Kotlin}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:${Deps.Version.Rx3Coroutine}")

    // Moshi
    implementation("com.squareup.moshi:moshi:${Deps.Version.Moshi}")
    implementation("com.squareup.moshi:moshi-kotlin:${Deps.Version.Moshi}")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:${Deps.Version.Retrofit}")
    implementation("com.squareup.retrofit2:adapter-rxjava3:${Deps.Version.Retrofit}")
    implementation("com.squareup.retrofit2:converter-moshi:${Deps.Version.Retrofit}")
    implementation("com.squareup.okhttp3:logging-interceptor:4.7.2")

    // RxJava
    implementation("io.reactivex.rxjava3:rxjava:${Deps.Version.RxJava}")
    implementation("io.reactivex.rxjava3:rxkotlin:${Deps.Version.RxKotlin}")
    implementation("io.reactivex.rxjava3:rxandroid:${Deps.Version.RxAndroid}")
    implementation("com.jakewharton.rxbinding4:rxbinding:${Deps.Version.RxBinding}")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:${Deps.Version.Hilt}")
    kapt("com.google.dagger:hilt-android-compiler:${Deps.Version.Hilt}")

    // AAC Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:${Deps.Version.Navigation}")
    implementation("androidx.navigation:navigation-ui-ktx:${Deps.Version.Navigation}")
    implementation("androidx.navigation:navigation-compose:2.5.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:28.1.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    // Paging
    implementation("androidx.paging:paging-runtime:${Deps.Version.Paging}")
    implementation("androidx.paging:paging-rxjava3:${Deps.Version.Paging}")

    // Compose
    implementation("androidx.compose.runtime:runtime:${Deps.Version.Compose}")
    implementation("androidx.compose.ui:ui:${Deps.Version.Compose}")
    implementation("androidx.compose.foundation:foundation:${Deps.Version.Compose}")
    implementation("androidx.compose.foundation:foundation-layout:${Deps.Version.Compose}")
    implementation("androidx.compose.material:material:${Deps.Version.Compose}")
    implementation("androidx.compose.runtime:runtime-livedata:${Deps.Version.Compose}")
    implementation("androidx.compose.ui:ui-tooling:${Deps.Version.Compose}")
    implementation("androidx.paging:paging-compose:${Deps.Version.PagingCompose}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation("androidx.compose.runtime:runtime-rxjava3:1.1.1")

    // misc
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("com.google.accompanist:accompanist-pager:0.20.3")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.20.3")
    implementation("com.facebook.android:facebook-login:15.0.1")
    implementation("de.psdev.licensesdialog:licensesdialog:2.1.0")
    implementation("com.uber.rxdogtag2:rxdogtag:2.0.1")
    implementation("com.github.skydoves:colorpickerview:2.2.3")
    implementation("com.jakewharton.timber:timber:4.7.1")
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.29.1-alpha")

    // coil
    implementation("io.coil-kt:coil-compose:2.1.0")
}

repositories {
    mavenCentral()
}
