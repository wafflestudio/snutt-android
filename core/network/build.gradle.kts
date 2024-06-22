plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.wafflestudio.snutt2.core.network"
}

dependencies {

    implementation( project (path = ":app", configuration = "stagingDebugApiElements")) // TODO : 최종적으로는 이게 없어야 한다. 일단은 임시로 추가해놓음

    implementation(libs.moshi)
    implementation(libs.hilt.android)
    implementation(libs.hilt.compiler)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.adapter.rxjava3)
    implementation(libs.retrofit.converter.moshi)
}