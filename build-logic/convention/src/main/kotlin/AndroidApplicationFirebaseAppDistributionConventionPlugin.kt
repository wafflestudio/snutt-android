import com.android.build.gradle.AppExtension
import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationFirebaseAppDistributionConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.firebase.appdistribution")
            extensions.configure<AppExtension> {
                productFlavors.all {
                    if (name == "staging") {
                        firebaseAppDistribution {
                            artifactType = "APK"
                            testers = "android-user"
                            serviceCredentialsFile = "gcp-service-account-staging.json"
                        }
                    }
                    if (name == "live") {
                        firebaseAppDistribution {
                            artifactType = "AAB"
                            testers = "android-user"
                            serviceCredentialsFile = "gcp-service-account-live.json"
                        }
                    }
                }
            }
        }
    }
}