package com.wafflestudio.snutt2.lib.logging

import android.os.Bundle

sealed class AnalyticsScreen {
    data object TimetableHome : AnalyticsScreen()
    data object TimetableMenu : AnalyticsScreen()
    data object TimetableShare : AnalyticsScreen()
    data object LectureCreate : AnalyticsScreen()
    data class LectureDetail(val parameter: LectureDetailParameter) : AnalyticsScreen()
    data object LectureList : AnalyticsScreen()
    data class LectureSyllabus(val parameter: LectureSyllabusParameter) : AnalyticsScreen()

    data object SearchHome : AnalyticsScreen()
    data object SearchEmpty : AnalyticsScreen()
    data object SearchList : AnalyticsScreen()
    data object SearchFilter : AnalyticsScreen()

    data object Bookmark : AnalyticsScreen()

    data object ReviewHome : AnalyticsScreen()
    data class ReviewDetail(val parameter: ReviewDetailParameter) : AnalyticsScreen()

    data object Friends : AnalyticsScreen()

    data object SettingsHome : AnalyticsScreen()
    data object SettingsAccount : AnalyticsScreen()
    data object SettingsTimetable : AnalyticsScreen()
    data object SettingsColorScheme : AnalyticsScreen()
    data object SettingsSupport : AnalyticsScreen()
    data object SettingsDevelopers : AnalyticsScreen()

    data object ThemeHome : AnalyticsScreen()
    data object ThemeBasicDetail : AnalyticsScreen()
    data object ThemeCustomNew : AnalyticsScreen()
    data object ThemeCustomEdit : AnalyticsScreen()
    data object ThemePreview : AnalyticsScreen()

    data object Vacancy : AnalyticsScreen()
    data object Popup : AnalyticsScreen()

    data object Login : AnalyticsScreen()
    data object Onboard : AnalyticsScreen()

    fun getExtraParameters(): Bundle {
        return when (this) {
            is LectureDetail -> parameter.toBundle()
            is ReviewDetail -> parameter.toBundle()
            is LectureSyllabus -> parameter.toBundle()
            else -> Bundle()
        }
    }
}

data class LectureDetailParameter(
    val lectureId: String,
    val referrer: DetailScreenReferrer?,
) {
    fun toBundle(): Bundle {
        return Bundle().apply {
            putString("lecture_id", lectureId)
            putString("referrer", referrer?.encode())
        }
    }
}

data class ReviewDetailParameter(
    val lectureID: String,
    val referrer: DetailScreenReferrer,
) {
    fun toBundle(): Bundle {
        return Bundle().apply {
            putString("lecture_id", lectureID)
            putString("referrer", referrer.encode())
        }
    }
}

data class LectureSyllabusParameter(
    val lectureID: String,
) {
    fun toBundle(): Bundle {
        return Bundle().apply {
            putString("lecture_id", lectureID)
        }
    }
}

sealed class DetailScreenReferrer {
    data class Search(val query: String) : DetailScreenReferrer()
    object Notification : DetailScreenReferrer()
    object Bookmark : DetailScreenReferrer()
    object Timetable : DetailScreenReferrer()
    object LectureList : DetailScreenReferrer()
    object LectureDetail : DetailScreenReferrer()

    fun encode(): String {
        return when (this) {
            is Search -> "search=$query"
            is Notification -> "notification"
            is Bookmark -> "bookmark"
            is Timetable -> "timetable"
            is LectureList -> "lectureList"
            is LectureDetail -> "lectureDetail"
        }
    }
}
