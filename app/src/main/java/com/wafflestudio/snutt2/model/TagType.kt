package com.wafflestudio.snutt2.model

/**
 * Created by makesource on 2017. 8. 26..
 */
enum class TagType(val isExclusive: Boolean) {
    SORT_CRITERIA(true),
    CLASSIFICATION(false),
    DEPARTMENT(false),
    ACADEMIC_YEAR(false),
    CREDIT(false),
    TIME(false),
    CATEGORY(false),
    CATEGORY_PRE2025(false),
    ETC(false),
}
