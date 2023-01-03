object SemVer {
    fun sementicVersionToSerializedCode(semanticVersion: String): Long {

        val semVerRegex = Regex("(\\d+).(\\d+).(\\d+)(-rc.(\\d+))?")

        val major = semVerRegex.find(semanticVersion)?.groupValues?.get(1)?.toLongOrNull() ?: 0L
        val minor = semVerRegex.find(semanticVersion)?.groupValues?.get(2)?.toLongOrNull() ?: 0L
        val patch = semVerRegex.find(semanticVersion)?.groupValues?.get(3)?.toLongOrNull() ?: 0L
        val releaseCandidate =
            semVerRegex.find(semanticVersion)?.groupValues?.get(5)?.toLongOrNull() ?: 999L

        return listOf(major, minor, patch, releaseCandidate)
            .fold(0) { acc, next ->
                acc * 100 + next
            } + 2009000000
    }
}
