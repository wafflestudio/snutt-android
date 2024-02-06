// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Deps.Version.AndroidGradle}")
        classpath("com.google.gms:google-services:4.3.15")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Deps.Version.Kotlin}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Deps.Version.Hilt}")
        classpath("com.google.firebase:firebase-appdistribution-gradle:4.0.0")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
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
        maven("https://naver.jfrog.io/artifactory/maven/")
    }
}

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}
