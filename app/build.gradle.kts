plugins {
    alias(libs.plugins.snutt.android.application)
    alias(libs.plugins.snutt.android.application.compose)
    alias(libs.plugins.snutt.android.application.flavors)
    alias(libs.plugins.snutt.android.hilt)
    alias(libs.plugins.snutt.android.application.firebase)
    alias(libs.plugins.snutt.android.application.firebase.app.distribution)
    alias(libs.plugins.snutt.semantic.versioning)

//    id("dagger.hilt.android.plugin")
//    id("kotlin-kapt")
}


android {
    namespace = "com.wafflestudio.snutt2"

    defaultConfig {
        applicationId = "com.wafflestudio.snutt2"
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
        debug {
            isDefault = true
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"))
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.named("release").get()
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.constraintlayout)
//    implementation(libs.androidx.compose.material3.windowSizeClass)
//    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
//    implementation(libs.androidx.profileinstaller)
//    implementation(libs.androidx.tracing.ktx)
//    implementation(libs.androidx.window.core)
//    implementation(libs.kotlinx.coroutines.guava)
//    implementation(libs.coil.kt)

    implementation(libs.material)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)

    ksp(libs.hilt.compiler)

    // TODO: Delete or Move
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.paging)
//    compileOnly(libs.kotlin.stdlib)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.adapter.rxjava3)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.okhttp.logging)
    implementation(libs.rxjava)
    implementation(libs.rxkotlin)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.core)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.material.core)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.hilt.navigation.compose)
    compileOnly(platform(libs.androidx.compose.bom))
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)
    implementation(libs.accompanist.navigation.material)
    implementation(libs.facebook.login)
    implementation(libs.coil.kt.compose)
    implementation(libs.licensesdialog)
    implementation(libs.timber)
    implementation(libs.gson)
    implementation(libs.naver.map)
    implementation(libs.naver.map.compose)
    implementation(libs.naver.map.compose)
    implementation(libs.google.id)
    implementation(libs.kakao.sdk)
    implementation(libs.kakao.sdk.share)
    implementation(libs.play.services.auth)

    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    implementation(libs.react.android)
    implementation(libs.hermes.android)
    implementation(fileTree(mapOf("dir" to "../libs", "include" to listOf("*.aar"))))

    // FIXME
    implementation(libs.firebase.cloud.messaging)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    // temp test
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:qualifiers"))
    implementation(project(":core:network"))

//    testImplementation("junit:junit:4.13.2")
//    implementation("androidx.legacy:legacy-support-v4:1.0.0")
//
//    implementation("org.jetbrains.kotlin:kotlin-reflect:${Deps.Version.Kotlin}")
}
