plugins {
    alias(libs.plugins.snutt.android.library)
    alias(libs.plugins.snutt.android.library.compose)
}

android {
    namespace = "com.wafflestudio.snutt2.core.designsystem"
}

dependencies {
    api(project(":core:model"))
    api(project(":core:designsystem"))

    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.material.core)
    api(libs.androidx.compose.material3.adaptive)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui.core)

    implementation(libs.coil.kt.compose)
}
