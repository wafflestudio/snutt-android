plugins {
    alias(libs.plugins.snutt.android.library)
    alias(libs.plugins.snutt.android.library.compose)
}

android {
    namespace = "com.wafflestudio.snutt2.core.ui"
}

dependencies {
    api(project(":core:model"))
}