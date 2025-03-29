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
    val provider: Provider
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
    val page: Int
) {
    fun toBundle(): Bundle {
        return Bundle().apply {
            putString("query", query)
            putString("quarter", quarter)
            putInt("page", page)
        }
    }
}

data class AddToBookmarkParameter(
    val lectureID: String,
    val referrer: LectureActionReferrer
) {
    fun toBundle(): Bundle {
        return Bundle().apply {
            putString("lecture_id", lectureID)
            putString("referrer", referrer.encode())
        }
    }
}

data class AddToTimetableParameter(
    val lectureID: String,
    val timetableID: String?,
    val referrer: LectureActionReferrer
) {
    fun toBundle(): Bundle {
        return Bundle().apply {
            putString("lecture_id", lectureID)
            timetableID?.let { putString("timetable_id", it) }
            putString("referrer", referrer.encode())
        }
    }
}

data class AddToVacancyParameter(
    val lectureID: String,
    val referrer: LectureActionReferrer
) {
    fun toBundle(): Bundle {
        return Bundle().apply {
            putString("lecture_id", lectureID)
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
