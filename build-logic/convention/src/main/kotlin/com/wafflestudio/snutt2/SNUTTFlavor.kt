package com.wafflestudio.snutt2
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor

@Suppress("EnumEntryName")
enum class FlavorDimension {
    mode
}

@Suppress("EnumEntryName")
enum class SNUTTFlavor(val dimension: FlavorDimension, val applicationIdSuffix: String) {
    // TODO: dev, prod 로 변경
    staging(FlavorDimension.mode, applicationIdSuffix = ".staging"),
    live(FlavorDimension.mode, applicationIdSuffix = ".live")
}

fun configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: SNUTTFlavor) -> Unit = {}
) {
    commonExtension.apply {
        flavorDimensions += FlavorDimension.mode.name
        productFlavors {
            SNUTTFlavor.values().forEach {
                create(it.name) {
                    dimension = it.dimension.name
                    flavorConfigurationBlock(this, it)
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        applicationIdSuffix = it.applicationIdSuffix
                    }
                }
            }
        }
    }
}
