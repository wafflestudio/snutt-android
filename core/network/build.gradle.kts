plugins {
    alias(libs.plugins.snutt.android.application)
    alias(libs.plugins.snutt.android.hilt)
}

android {
    namespace = "com.wafflestudio.snutt2.core.network"
}

dependencies {

    implementation( project (path = ":app", configuration = "stagingDebugApiElements")) // TODO : 일단 임시로 가져옴

    implementation(libs.gson)
    implementation(libs.timber)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.hilt.android)
    implementation(libs.hilt.compiler)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.adapter.rxjava3)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.naver.map)
}