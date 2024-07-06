import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.wafflestudio.snutt2.configureSecrets
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class BuildConfigSecretsConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            plugins.withId("com.android.application") {
                val extension = extensions.getByType<ApplicationExtension>()
                configureSecrets(extension)
            }
            plugins.withId("com.android.library") {
                val extension = extensions.getByType<LibraryExtension>()
                configureSecrets(extension)
            }
        }
    }
}