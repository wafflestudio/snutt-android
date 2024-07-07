plugins {
    alias(libs.plugins.snutt.android.library)
    alias(libs.plugins.snutt.android.hilt)
}

android {
    namespace = "com.wafflestudio.snutt2.core.network"
    buildFeatures{
        buildConfig = true
    }
}

dependencies {
    implementation(libs.gson)
    implementation(libs.timber)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
//    implementation(libs.hilt.android)
//    implementation(libs.hilt.compiler)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.adapter.rxjava3)
    implementation(libs.retrofit.converter.moshi)

    // temp test
    implementation(project(":core:qualifiers"))
}