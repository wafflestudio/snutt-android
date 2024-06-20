import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Properties

class SemanticVersioningConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.create("versioning", SemanticVersioningUtils::class.java)
            extensions.configure<AppExtension> {
                val versionProps = Properties().apply {
                    load(Files.newBufferedReader(Paths.get(rootProject.rootDir.toString(), "version.properties")))
                }

                defaultConfig {
                    val propertyVersionName = versionProps.getProperty("snuttVersionName")
                    versionName = propertyVersionName
                    versionCode = extensions.getByType<SemanticVersioningUtils>()
                        .semanticVersionToSerializedCode(propertyVersionName)
                }
            }
        }
    }
}

open class SemanticVersioningUtils {
    fun semanticVersionToSerializedCode(semanticVersion: String): Int {

        val semVerRegex = Regex("(\\d+).(\\d+).(\\d+)(-rc.(\\d+))?")

        val major = semVerRegex.find(semanticVersion)?.groupValues?.get(1)?.toIntOrNull() ?: 0
        val minor = semVerRegex.find(semanticVersion)?.groupValues?.get(2)?.toIntOrNull() ?: 0
        val patch = semVerRegex.find(semanticVersion)?.groupValues?.get(3)?.toIntOrNull() ?: 0

        return listOf(major, minor, patch)
            .fold(0) { acc, next ->
                acc * 100 + next
            } + 2010000000
    }
}