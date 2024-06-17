import org.gradle.api.Plugin
import org.gradle.api.Project

class SemanticVersioningConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.create("versioning", SemanticVersioningUtils::class.java)
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