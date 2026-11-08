package com.wafflestudio.snutt2.logging

import android.os.Bundle

/**
 * [name] 은 Firebase Analytics 에 기록되는 이름이다. 클래스명에서 유도하면 R8 난독화 시 이름이 바뀌므로
 * 반드시 문자열로 명시한다. 기존 BigQuery 데이터와 이어지도록 한 번 정한 값은 바꾸지 않는다.
 */
sealed class AnalyticsEvent(val name: String) {
    data class Login(val parameter: LoginParameter) : AnalyticsEvent("login")
    data object Logout : AnalyticsEvent("logout")
    data object SignUp : AnalyticsEvent("sign_up")
    data class SearchLecture(val parameter: SearchLectureParameter) : AnalyticsEvent("search_lecture")
    data class AddToBookmark(val parameter: AddToBookmarkParameter) : AnalyticsEvent("add_to_bookmark")
    data class AddToTimetable(val parameter: AddToTimetableParameter) : AnalyticsEvent("add_to_timetable")
    data class AddToVacancy(val parameter: AddToVacancyParameter) : AnalyticsEvent("add_to_vacancy")
    data object DiaryFirstSectionDone : AnalyticsEvent("diary_first_section_done")
    data class DiarySubmitted(val parameter: DiarySubmittedParameter) : AnalyticsEvent("diary_submitted")
    data class DiaryWriteExit(val parameter: DiaryWriteExitParameter) : AnalyticsEvent("diary_write_exit")
    data class DiaryAfterSubmit(val parameter: DiaryAfterSubmitParameter) : AnalyticsEvent("diary_after_submit")

    fun getExtraParameters(): Bundle = when (this) {
        is Login -> parameter.toBundle()
        is SearchLecture -> parameter.toBundle()
        is AddToBookmark -> parameter.toBundle()
        is AddToTimetable -> parameter.toBundle()
        is AddToVacancy -> parameter.toBundle()
        is DiaryAfterSubmit -> parameter.toBundle()
        is DiarySubmitted -> parameter.toBundle()
        is DiaryWriteExit -> parameter.toBundle()
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

/**
 * @param activityTypeIds 선택한 활동(DiaryDailyClassType)의 서버 id. 표시 이름은 바뀔 수 있으므로 id 를 기록한다.
 * @param hasComment 코멘트 작성 여부. 코멘트 본문은 기록하지 않는다.
 * @param elapsedSec 작성 화면 진입 후 제출 성공까지 걸린 시간(초).
 */
data class DiarySubmittedParameter(
    val activityTypeIds: List<String>,
    val hasComment: Boolean,
    val elapsedSec: Long,
    val referrer: DiaryWriteReferrer,
    val pushId: String?,
) {
    fun toBundle(): Bundle = Bundle().apply {
        putString("activity_types", activityTypeIds.joinToString(","))
        putString("has_comment", hasComment.toString())
        putLong("elapsed_sec", elapsedSec)
        putDiaryWriteSource(referrer, pushId)
    }
}

/**
 * @param step 이탈 시점의 단계. 활동 선택을 한 번도 완료하지 않았으면 [Step.ACTIVITY], 질문 단계에 도달했으면 [Step.QUESTIONS].
 * @param elapsedSec 작성 화면 진입 후 이탈까지 걸린 시간(초).
 */
data class DiaryWriteExitParameter(
    val step: Step,
    val elapsedSec: Long,
    val referrer: DiaryWriteReferrer,
    val pushId: String?,
) {
    enum class Step(val value: String) {
        ACTIVITY("activity"),
        QUESTIONS("questions"),
    }

    fun toBundle(): Bundle = Bundle().apply {
        putString("step", step.value)
        putLong("elapsed_sec", elapsedSec)
        putDiaryWriteSource(referrer, pushId)
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
