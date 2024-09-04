package com.wafflestudio.snutt2.model

/**
 * Created by makesource on 2017. 8. 26..
 */
sealed class TagType(val isExclusive: Boolean) {
    data object Classification : TagType(false)
    data object Department : TagType(false)
    data object AcademicYear : TagType(false)
    data object Credit : TagType(false)
    data object Time : TagType(false)
    data object Category : TagType(false)
    data object SortCriteria : TagType(true)
    data object Etc : TagType(false)
}
