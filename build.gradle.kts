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
    }
}

plugins {
    id("org.jlleitschuh.gradle.ktlint-idea") version "9.4.1"
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
}

allprojects {
    repositories {
        google()
        maven(url = "https://jitpack.io")
        mavenCentral()
    }
}

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}
