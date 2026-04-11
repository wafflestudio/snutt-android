package com.wafflestudio.snutt2.logging

import android.os.Bundle

sealed class AnalyticsEvent {
    data class Login(val parameter: LoginParameter) : AnalyticsEvent()
    data object Logout : AnalyticsEvent()
    data object SignUp : AnalyticsEvent()
    data class SearchLecture(val parameter: SearchLectureParameter) : AnalyticsEvent()
    data class AddToBookmark(val parameter: AddToBookmarkParameter) : AnalyticsEvent()
    data class AddToTimetable(val parameter: AddToTimetableParameter) : AnalyticsEvent()
    data class AddToVacancy(val parameter: AddToVacancyParameter) : AnalyticsEvent()
    data object DiaryFirstSectionDone : AnalyticsEvent()
    data object DiarySubmitted : AnalyticsEvent()
    data class DiaryAfterSubmit(val parameter: DiaryAfterSubmitParameter) : AnalyticsEvent()

    fun getExtraParameters(): Bundle {
        return when (this) {
            is Login -> parameter.toBundle()
            is SearchLecture -> parameter.toBundle()
            is AddToBookmark -> parameter.toBundle()
            is AddToTimetable -> parameter.toBundle()
            is AddToVacancy -> parameter.toBundle()
            is DiaryAfterSubmit -> parameter.toBundle()
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

data class DiaryAfterSubmitParameter(
    val action: Action,
) {
    enum class Action {
        NEXT, HOME, REVIEW
    }

    fun toBundle(): Bundle {
        return Bundle().apply {
            putString("action", action.name.lowercase())
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
