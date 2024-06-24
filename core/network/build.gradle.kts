plugins {
    alias(libs.plugins.snutt.android.application)
    alias(libs.plugins.snutt.android.hilt)
}

android {
    namespace = "com.wafflestudio.snutt2.core.network"
}

dependencies {

    implementation( project (path = ":app", configuration = "stagingDebugApiElements"))

    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.hilt.android)
    implementation(libs.hilt.compiler)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.adapter.rxjava3)
    implementation(libs.retrofit.converter.moshi)
}