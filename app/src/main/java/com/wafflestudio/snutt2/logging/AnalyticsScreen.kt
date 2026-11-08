package com.wafflestudio.snutt2.logging

import android.os.Bundle

/**
 * [name] 은 Firebase Analytics 에 기록되는 이름이다. 클래스명에서 유도하면 R8 난독화 시 이름이 바뀌므로
 * 반드시 문자열로 명시한다. 기존 BigQuery 데이터와 이어지도록 한 번 정한 값은 바꾸지 않는다.
 */
sealed class AnalyticsScreen(val name: String) {
    data object TimetableHome : AnalyticsScreen("timetable_home")
    data object TimetableMenu : AnalyticsScreen("timetable_menu")
    data object TimetableShare : AnalyticsScreen("timetable_share")
    data object LectureCreate : AnalyticsScreen("lecture_create")
    data class LectureDetail(val parameter: LectureDetailParameter) : AnalyticsScreen("lecture_detail")
    data object LectureList : AnalyticsScreen("lecture_list")
    data class LectureSyllabus(val parameter: LectureSyllabusParameter) : AnalyticsScreen("lecture_syllabus")

    data object SearchHome : AnalyticsScreen("search_home")
    data object SearchEmpty : AnalyticsScreen("search_empty")
    data object SearchList : AnalyticsScreen("search_list")
    data object SearchFilter : AnalyticsScreen("search_filter")

    data object Bookmark : AnalyticsScreen("bookmark")

    data object ReviewHome : AnalyticsScreen("review_home")
    data class ReviewDetail(val parameter: ReviewDetailParameter) : AnalyticsScreen("review_detail")

    data object Friends : AnalyticsScreen("friends")

    data object SettingsHome : AnalyticsScreen("settings_home")
    data object SettingsAccount : AnalyticsScreen("settings_account")
    data object SettingsTimetable : AnalyticsScreen("settings_timetable")
    data object SettingsColorScheme : AnalyticsScreen("settings_color_scheme")
    data object SettingsSupport : AnalyticsScreen("settings_support")
    data object SettingsDevelopers : AnalyticsScreen("settings_developers")

    data object ThemeHome : AnalyticsScreen("theme_home")
    data object ThemeBasicDetail : AnalyticsScreen("theme_basic_detail")
    data object ThemeCustomNew : AnalyticsScreen("theme_custom_new")
    data object ThemeCustomEdit : AnalyticsScreen("theme_custom_edit")
    data object ThemeDownloaded : AnalyticsScreen("theme_downloaded")
    data object ThemePreview : AnalyticsScreen("theme_preview")

    data class DiaryWrite(val parameter: DiaryWriteParameter) : AnalyticsScreen("diary_write")
    data object DiaryHistory : AnalyticsScreen("diary_history")
    data object Vacancy : AnalyticsScreen("vacancy")
    data object Popup : AnalyticsScreen("popup")

    data object Login : AnalyticsScreen("login")
    data object Onboard : AnalyticsScreen("onboard")

    fun getExtraParameters(): Bundle? = when (this) {
        is LectureDetail -> parameter.toBundle()
        is ReviewDetail -> parameter.toBundle()
        is LectureSyllabus -> parameter.toBundle()
        is DiaryWrite -> parameter.toBundle()
        else -> null
    }
}

data class LectureDetailParameter(
    val lectureId: String,
    val referrer: DetailScreenReferrer?,
) {
    fun toBundle(): Bundle = Bundle().apply {
        putString("lecture_id", lectureId)
        putString("referrer", referrer?.encode())
    }
}

data class ReviewDetailParameter(
    val lectureId: String,
    val referrer: DetailScreenReferrer,
) {
    fun toBundle(): Bundle = Bundle().apply {
        putString("lecture_id", lectureId)
        putString("referrer", referrer.encode())
    }
}

data class LectureSyllabusParameter(
    val lectureId: String,
) {
    fun toBundle(): Bundle = Bundle().apply {
        putString("lecture_id", lectureId)
    }
}

data class DiaryWriteParameter(
    val referrer: DiaryWriteReferrer,
    val pushId: String?,
) {
    fun toBundle(): Bundle = Bundle().apply {
        putDiaryWriteSource(referrer, pushId)
    }
}

/** 강의일기 작성 화면에 진입한 경로. [value] 는 Firebase 에 기록되는 값이자 navigation argument 값이다. */
enum class DiaryWriteReferrer(val value: String) {
    PUSH("push"),
    HISTORY("history"),
    NEXT("next"),
    ;

    companion object {
        /** referrer 가 없는 진입(딥링크)은 푸시로 간주한다. */
        fun decode(value: String?): DiaryWriteReferrer = entries.firstOrNull { it.value == value } ?: PUSH
    }
}

internal fun Bundle.putDiaryWriteSource(referrer: DiaryWriteReferrer, pushId: String?) {
    putString("referrer", referrer.value)
    pushId?.let { putString("push_id", it) }
}

sealed class DetailScreenReferrer {
    data class Search(val query: String) : DetailScreenReferrer()
    data object Notification : DetailScreenReferrer()
    data object Bookmark : DetailScreenReferrer()
    data object Timetable : DetailScreenReferrer()
    data object LectureList : DetailScreenReferrer()
    data object LectureDetail : DetailScreenReferrer()

    fun encode(): String = when (this) {
        is Search -> "search=$query"
        is Notification -> "notification"
        is Bookmark -> "bookmark"
        is Timetable -> "timetable"
        is LectureList -> "lectureList"
        is LectureDetail -> "lectureDetail"
    }

    companion object {
        /** [encode]의 역연산. Search만 query 파라미터를 포함하므로 prefix 매칭으로 처리한다. */
        fun decode(value: String): DetailScreenReferrer? = when {
            value.startsWith("search=") -> Search(value.removePrefix("search="))
            value == "notification" -> Notification
            value == "bookmark" -> Bookmark
            value == "timetable" -> Timetable
            value == "lectureList" -> LectureList
            value == "lectureDetail" -> LectureDetail
            else -> null
        }
    }
}
