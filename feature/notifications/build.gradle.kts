plugins {
    alias(libs.plugins.snutt.android.feature)
    alias(libs.plugins.snutt.android.library.compose)
}

android {
    namespace = "com.wafflestudio.snutt2.feature.notifications"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:ui"))
}
