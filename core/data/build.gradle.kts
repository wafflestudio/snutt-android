plugins {
    alias(libs.plugins.snutt.android.library)
    alias(libs.plugins.snutt.android.hilt)
}

android {
    namespace = "com.wafflestudio.snutt2.core.data"
}

dependencies {
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.androidx.paging.compose)

    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:model")) // TODO : revisit
    implementation(project(":core:qualifiers")) // TODO : revisit
}