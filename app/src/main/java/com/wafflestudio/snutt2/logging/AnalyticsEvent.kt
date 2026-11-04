package com.wafflestudio.snutt2.logging

import android.os.Bundle

// 분석용 이름은 난독화와 클래스 이름 변경의 영향을 받지 않도록 고정한다.
sealed class AnalyticsEvent(val eventName: String) {
    data class Login(val parameter: LoginParameter) : AnalyticsEvent("login")
    data object Logout : AnalyticsEvent("logout")
    data object SignUp : AnalyticsEvent("sign_up")
    data class SearchLecture(val parameter: SearchLectureParameter) : AnalyticsEvent("search_lecture")
    data class AddToBookmark(val parameter: AddToBookmarkParameter) : AnalyticsEvent("add_to_bookmark")
    data class AddToTimetable(val parameter: AddToTimetableParameter) : AnalyticsEvent("add_to_timetable")
    data class AddToVacancy(val parameter: AddToVacancyParameter) : AnalyticsEvent("add_to_vacancy")
    data object DiaryFirstSectionDone : AnalyticsEvent("diary_first_section_done")
    data object DiarySubmitted : AnalyticsEvent("diary_submitted")
    data class DiaryAfterSubmit(val parameter: DiaryAfterSubmitParameter) : AnalyticsEvent("diary_after_submit")

    fun getExtraParameters(): Bundle = when (this) {
        is Login -> parameter.toBundle()
        is SearchLecture -> parameter.toBundle()
        is AddToBookmark -> parameter.toBundle()
        is AddToTimetable -> parameter.toBundle()
        is AddToVacancy -> parameter.toBundle()
        is DiaryAfterSubmit -> parameter.toBundle()
        else -> Bundle()
    }
}

data class LoginParameter(
    val provider: Provider,
) {
    enum class Provider {
        LOCAL,
        GOOGLE,
        APPLE,
        FACEBOOK,
        KAKAO,
    }

    fun toBundle(): Bundle = Bundle().apply {
        putString("provider", provider.name.lowercase())
    }
}

data class SearchLectureParameter(
    val query: String,
    val quarter: String,
) {
    fun toBundle(): Bundle = Bundle().apply {
        putString("query", query)
        putString("quarter", quarter)
    }
}

data class AddToBookmarkParameter(
    val lectureId: String,
    val referrer: LectureActionReferrer,
) {
    fun toBundle(): Bundle = Bundle().apply {
        putString("lecture_id", lectureId)
        putString("referrer", referrer.encode())
    }
}

data class AddToTimetableParameter(
    val lectureId: String,
    val timetableId: String?,
    val referrer: LectureActionReferrer,
) {
    fun toBundle(): Bundle = Bundle().apply {
        putString("lecture_id", lectureId)
        timetableId?.let { putString("timetable_id", it) }
        putString("referrer", referrer.encode())
    }
}

data class AddToVacancyParameter(
    val lectureId: String,
    val referrer: LectureActionReferrer,
) {
    fun toBundle(): Bundle = Bundle().apply {
        putString("lecture_id", lectureId)
        putString("referrer", referrer.encode())
    }
}

data class DiaryAfterSubmitParameter(
    val action: Action,
) {
    enum class Action {
        NEXT,
        HOME,
        REVIEW,
    }

    fun toBundle(): Bundle = Bundle().apply {
        putString("action", action.name.lowercase())
    }
}

sealed class LectureActionReferrer {
    data class Search(val query: String) : LectureActionReferrer()
    data object LectureDetail : LectureActionReferrer()
    data object Bookmark : LectureActionReferrer()

    fun encode(): String = when (this) {
        is Search -> "search=$query"
        is LectureDetail -> "lectureDetail"
        is Bookmark -> "bookmark"
    }
}
