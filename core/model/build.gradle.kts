plugins {
    alias(libs.plugins.snutt.android.library)
    //alias(libs.plugins.snutt.jvm.library)
}

android {
    namespace = "com.wafflestudio.snutt2.core.model"

    signingConfigs {
        create("release") {
            keyAlias = System.getenv("SIGNING_KEY_ALIAS")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
            storeFile = file("keystore/android.jks")
            storePassword = System.getenv("SIGNING_STORE_PASSWORD")
        }
    }

    buildTypes {
        debug {
            isDefault = true
            isMinifyEnabled = false
            //consumerProguardFiles(getDefaultProguardFile("proguard-android.txt"))
        }

        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.named("release").get()
            consumerProguardFiles(
                "proguard-rules.pro",
            )
        }
    }
}