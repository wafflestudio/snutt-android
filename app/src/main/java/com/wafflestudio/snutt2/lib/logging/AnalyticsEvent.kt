package com.wafflestudio.snutt2.lib.logging

import android.os.Bundle

sealed class AnalyticsEvent {
    data class Login(val parameter: LoginParameter) : AnalyticsEvent()
    data object Logout : AnalyticsEvent()
    data object SignUp : AnalyticsEvent()
    data class SearchLecture(val parameter: SearchLectureParameter) : AnalyticsEvent()
    data class AddToBookmark(val parameter: AddToBookmarkParameter) : AnalyticsEvent()
    data class AddToTimetable(val parameter: AddToTimetableParameter) : AnalyticsEvent()
    data class AddToVacancy(val parameter: AddToVacancyParameter) : AnalyticsEvent()

    fun getExtraParameters(): Bundle {
        return when (this) {
            is Login -> parameter.toBundle()
            is SearchLecture -> parameter.toBundle()
            is AddToBookmark -> parameter.toBundle()
            is AddToTimetable -> parameter.toBundle()
            is AddToVacancy -> parameter.toBundle()
            else -> Bundle()
        }
    }
}

data class LoginParameter(
    val provider: Provider,
) {
    enum class Provider {
        LOCAL, GOOGLE, APPLE, FACEBOOK, KAKAO
    }

    fun toBundle(): Bundle {
        return Bundle().apply {
            putString("provider", provider.name.lowercase())
        }
    }
}

data class SearchLectureParameter(
    val query: String,
    val quarter: String,
) {
    fun toBundle(): Bundle {
        return Bundle().apply {
            putString("query", query)
            putString("quarter", quarter)
        }
    }
}

data class AddToBookmarkParameter(
    val lectureId: String,
    val referrer: LectureActionReferrer,
) {
    fun toBundle(): Bundle {
        return Bundle().apply {
            putString("lecture_id", lectureId)
            putString("referrer", referrer.encode())
        }
    }
}

data class AddToTimetableParameter(
    val lectureId: String,
    val timetableId: String?,
    val referrer: LectureActionReferrer,
) {
    fun toBundle(): Bundle {
        return Bundle().apply {
            putString("lecture_id", lectureId)
            timetableId?.let { putString("timetable_id", it) }
            putString("referrer", referrer.encode())
        }
    }
}

data class AddToVacancyParameter(
    val lectureId: String,
    val referrer: LectureActionReferrer,
) {
    fun toBundle(): Bundle {
        return Bundle().apply {
            putString("lecture_id", lectureId)
            putString("referrer", referrer.encode())
        }
    }
}

sealed class LectureActionReferrer {
    data class Search(val query: String) : LectureActionReferrer()
    data object LectureDetail : LectureActionReferrer()
    data object Bookmark : LectureActionReferrer()

    fun encode(): String {
        return when (this) {
            is Search -> "search=$query"
            is LectureDetail -> "lectureDetail"
            is Bookmark -> "bookmark"
        }
    }
}
