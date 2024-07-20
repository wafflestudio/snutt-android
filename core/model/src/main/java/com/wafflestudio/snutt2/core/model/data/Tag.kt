package com.wafflestudio.snutt2.core.model.data

data class Tag(
    val type: Type,
    val name: String,
) {
    companion object {
        val TIME_EMPTY: Tag = Tag(Type.TIME, "빈 시간대로 검색")
        val TIME_SELECT: Tag = Tag(Type.TIME, "시간대 직접 선택")
        val ETC_ENG: Tag = Tag(Type.ETC, "영어진행 강의")
        val ETC_MILITARY: Tag = Tag(Type.ETC, "군휴학 원격수업")
    }

    enum class Type {
        CLASSIFICATION,
        DEPARTMENT,
        ACADEMIC_YEAR,
        CREDIT,
        TIME,
        CATEGORY,
        ETC,
    }
}
