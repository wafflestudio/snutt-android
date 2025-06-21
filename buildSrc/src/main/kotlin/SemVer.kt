object SemVer {
    fun sementicVersionToSerializedCode(semanticVersion: String): Long {

        val semVerRegex = Regex("(\\d+).(\\d+).(\\d+)(-rc.(\\d+))?")

        val major = semVerRegex.find(semanticVersion)?.groupValues?.get(1)?.toLongOrNull() ?: 0L
        val minor = semVerRegex.find(semanticVersion)?.groupValues?.get(2)?.toLongOrNull() ?: 0L
        val patch = semVerRegex.find(semanticVersion)?.groupValues?.get(3)?.toLongOrNull() ?: 0L
        // group 4는 "rc-N" 전체이고, group 5가 N이다.
        val rc = semVerRegex.find(semanticVersion)?.groupValues?.get(5)?.toLongOrNull() ?: 99L

        return listOf(major, minor, patch, rc)
            .fold(0L) { acc, next ->
                acc * 100 + next
            } + 2100000000L
    }
}
