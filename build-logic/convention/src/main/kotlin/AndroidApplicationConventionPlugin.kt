import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.BaseExtension
//import com.wafflestudio.snutt2.configureBadgingTasks
//import com.google.samples.apps.nowinandroid.configureGradleManagedDevices
import com.wafflestudio.snutt2.configureKotlinAndroid
//import com.google.samples.apps.nowinandroid.configurePrintApksTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 34
                @Suppress("UnstableApiUsage")
                testOptions.animationsDisabled = true
//                configureGradleManagedDevices(this)
            }
//            extensions.configure<ApplicationAndroidComponentsExtension> {
//                configurePrintApksTask(this)
//                configureBadgingTasks(extensions.getByType<BaseExtension>(), this)
//            }
        }
    }

}
