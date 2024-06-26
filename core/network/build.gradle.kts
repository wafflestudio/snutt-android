plugins {
    alias(libs.plugins.snutt.android.application)
}

android {
    namespace = "com.wafflestudio.snutt2.core.network"
}

dependencies {
    implementation(libs.retrofit.converter.moshi)
}