plugins {
    alias(libs.plugins.snutt.android.library)
    alias(libs.plugins.snutt.android.library.compose)
}

android {
    namespace = "com.wafflestudio.snutt2.core.ui"
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:model"))
}

dependencies {
    implementation(project(":core:model"))

    implementation(libs.coil.kt.compose)
}