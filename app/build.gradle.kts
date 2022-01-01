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
    compileSdk = 30

    repositories {
        mavenCentral()
    }

    defaultConfig {
        applicationId = "com.wafflestudio.snutt2"
        minSdk = 24
        targetSdk = 30
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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }


    viewBinding {
        isEnabled = true
    }

    flavorDimensions.add("mode")

    productFlavors {
        create("staging") {
            applicationIdSuffix = ".staging"
            versionCode = versionProps.getProperty("snuttVersionCode").toInt()
            versionName = versionProps.getProperty("snuttVersionName")
        }

        create("live") {
            applicationIdSuffix = ".live"
            versionCode = versionProps.getProperty("snuttVersionCode").toInt()
            versionName = versionProps.getProperty("snuttVersionName")
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    testImplementation("junit:junit:4.12")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.2")

    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation("com.appyvet:materialrangebar:1.3")

    implementation("com.github.JakeWharton:ViewPagerIndicator:2.4.1")

    implementation("com.google.code.gson:gson:2.8.6")

    // Moshi
    implementation("com.squareup.moshi:moshi:1.12.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava3:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.7.2")

    // RxJava
    implementation("io.reactivex.rxjava3:rxjava:3.0.12")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding:4.0.0")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.36")
    kapt("com.google.dagger:hilt-android-compiler:2.36")

    // AAC Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:28.1.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Paging
    implementation("androidx.paging:paging-runtime:3.0.0")
    implementation("androidx.paging:paging-rxjava3:3.0.0")

    // misc
    implementation("androidx.drawerlayout:drawerlayout:1.1.1")
    implementation("com.facebook.android:facebook-login:11.1.0")
    implementation("de.psdev.licensesdialog:licensesdialog:2.1.0")
    implementation("com.uber.rxdogtag2:rxdogtag:2.0.1")
    implementation("com.github.skydoves:colorpickerview:2.2.3")
    implementation("com.jakewharton.timber:timber:4.7.1")
    implementation("com.tbuonomo:dotsindicator:4.2")
}

repositories {
    mavenCentral()
}
