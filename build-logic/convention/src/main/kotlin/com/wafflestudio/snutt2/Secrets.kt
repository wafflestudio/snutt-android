package com.wafflestudio.snutt2

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import java.io.File
import java.util.Properties

internal fun Project.configureSecrets(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    commonExtension.apply {
        buildFeatures.buildConfig = true
        productFlavors {
            SNUTTFlavor.values().forEach {
                val flavor = getByName(it.name)
                val flavorPropertiesFile =
                    project.rootProject.file("gradle-${flavor.name}.properties")
                val flavorProperties = loadProperties(flavorPropertiesFile)
                flavorProperties.forEach { key, value ->
                    flavor.buildConfigField("String", key as String, value as String)
                }
            }
        }
    }
}

private fun loadProperties(file: File): Properties {
    val properties = Properties()
    if (file.exists()) {
        file.inputStream().use { properties.load(it) }
    }
    return properties
}