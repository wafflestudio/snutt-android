plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.jetbrains.kotlin.android) // TODO : 밑의 kotlinOptions 때문에 얘가 필요해서 자동으로 추가됨
}

android {
    compileSdk = 34 // TODO : 이것까지 없애버렸더니 spotlessApply가 안돌아감
    namespace = "com.wafflestudio.snutt2.core.network"
    kotlinOptions { // TODO : 이것까지 없애버렸더니 core/network의 파일들에서 kotlin not configured라고 뜸
        jvmTarget = "17"
    }
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