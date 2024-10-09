object SemVer {
    fun sementicVersionToSerializedCode(semanticVersion: String): Long {

        val semVerRegex = Regex("(\\d+).(\\d+).(\\d+)(-rc.(\\d+))?")

        val major = semVerRegex.find(semanticVersion)?.groupValues?.get(1)?.toLongOrNull() ?: 0L
        val minor = semVerRegex.find(semanticVersion)?.groupValues?.get(2)?.toLongOrNull() ?: 0L
        val patch = semVerRegex.find(semanticVersion)?.groupValues?.get(3)?.toLongOrNull() ?: 0L

        return listOf(major, minor, patch)
            .fold(0L) { acc, next ->
                acc * 100 + next
            } + 2010000000L
    }
}
