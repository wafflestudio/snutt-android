// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("com.google.gms:google-services:4.3.10")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Deps.Version.Kotlin}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Deps.Version.Hilt}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Deps.Version.Navigation}")
        classpath("com.google.firebase:firebase-appdistribution-gradle:3.0.0")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.8.1")
    }
}

plugins {
    id("org.jlleitschuh.gradle.ktlint-idea") version Deps.Version.KtLintGradle
    id("org.jlleitschuh.gradle.ktlint") version Deps.Version.KtLintGradle
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}
