package com.wafflestudio.snutt2.model

/**
 * Created by makesource on 2017. 8. 26..
 */
sealed class TagType(val isExclusive: Boolean) {
    data object CLASSIFICATION : TagType(false)
    data object DEPARTMENT : TagType(false)
    data object ACADEMIC_YEAR : TagType(false)
    data object CREDIT : TagType(false)
    data object TIME : TagType(false)
    data object CATEGORY : TagType(false)
    data object SORT_CRITERIA : TagType(true)
    data object ETC : TagType(false)
}
