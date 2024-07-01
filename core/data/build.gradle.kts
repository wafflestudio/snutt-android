plugins {
    alias(libs.plugins.snutt.android.library)
    alias(libs.plugins.snutt.android.hilt)
}

android {
    namespace = "com.wafflestudio.snutt2.core.database"
}

dependencies {
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)

    // temp test
    implementation(project(":core:network"))
}