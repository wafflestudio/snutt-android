package com.wafflestudio.snutt2.logging

import android.os.Bundle
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** 클래스·프로퍼티 이름을 정리해도 기존 분석 데이터의 key와 value는 유지되어야 한다. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AnalyticsParameterContractTest {
    @Test
    fun `login providers retain their lowercase wire values`() {
        val providers = mapOf(
            LoginParameter.Provider.LOCAL to "local",
            LoginParameter.Provider.GOOGLE to "google",
            LoginParameter.Provider.APPLE to "apple",
            LoginParameter.Provider.FACEBOOK to "facebook",
            LoginParameter.Provider.KAKAO to "kakao",
        )
        providers.forEach { (provider, value) ->
            assertEquals(mapOf("provider" to value), AnalyticsEvent.Login(LoginParameter(provider)).getExtraParameters().values())
        }
    }

    @Test
    fun `search event retains query and quarter keys`() {
        assertEquals(
            mapOf("query" to "운영체제 & network", "quarter" to "2026-2"),
            AnalyticsEvent.SearchLecture(SearchLectureParameter("운영체제 & network", "2026-2")).getExtraParameters().values(),
        )
    }

    @Test
    fun `bookmark event retains lecture ID and search referrer`() {
        assertEquals(
            mapOf("lecture_id" to "lecture-42", "referrer" to "search=운영체제=심화"),
            AnalyticsEvent.AddToBookmark(
                AddToBookmarkParameter("lecture-42", LectureActionReferrer.Search("운영체제=심화")),
            ).getExtraParameters().values(),
        )
    }

    @Test
    fun `timetable event includes a provided timetable ID`() {
        assertEquals(
            mapOf("lecture_id" to "lecture-42", "timetable_id" to "table-73", "referrer" to "lectureDetail"),
            AnalyticsEvent.AddToTimetable(
                AddToTimetableParameter("lecture-42", "table-73", LectureActionReferrer.LectureDetail),
            ).getExtraParameters().values(),
        )
    }

    @Test
    fun `timetable event omits an absent timetable ID`() {
        assertEquals(
            mapOf("lecture_id" to "lecture-42", "referrer" to "bookmark"),
            AnalyticsEvent.AddToTimetable(
                AddToTimetableParameter("lecture-42", null, LectureActionReferrer.Bookmark),
            ).getExtraParameters().values(),
        )
    }

    @Test
    fun `vacancy event retains lecture ID and referrer`() {
        assertEquals(
            mapOf("lecture_id" to "lecture-42", "referrer" to "bookmark"),
            AnalyticsEvent.AddToVacancy(
                AddToVacancyParameter("lecture-42", LectureActionReferrer.Bookmark),
            ).getExtraParameters().values(),
        )
    }

    @Test
    fun `diary actions retain their lowercase wire values`() {
        val actions = mapOf(
            DiaryAfterSubmitParameter.Action.NEXT to "next",
            DiaryAfterSubmitParameter.Action.HOME to "home",
            DiaryAfterSubmitParameter.Action.REVIEW to "review",
        )
        actions.forEach { (action, value) ->
            assertEquals(
                mapOf("action" to value),
                AnalyticsEvent.DiaryAfterSubmit(DiaryAfterSubmitParameter(action)).getExtraParameters().values(),
            )
        }
    }

    @Test
    fun `detail referrers retain their encoded values and decode correctly`() {
        val referrers = mapOf(
            DetailScreenReferrer.Search("한글 &=검색") to "search=한글 &=검색",
            DetailScreenReferrer.Search("") to "search=",
            DetailScreenReferrer.Notification to "notification",
            DetailScreenReferrer.Bookmark to "bookmark",
            DetailScreenReferrer.Timetable to "timetable",
            DetailScreenReferrer.LectureList to "lectureList",
            DetailScreenReferrer.LectureDetail to "lectureDetail",
        )
        referrers.forEach { (referrer, encoded) ->
            assertEquals(encoded, referrer.encode())
            assertEquals(referrer, DetailScreenReferrer.decode(encoded))
        }
    }

    @Test
    fun `unknown detail referrers do not decode`() {
        listOf("", "lecture_list", "SEARCH=query", "search", "unknown").forEach {
            assertNull(DetailScreenReferrer.decode(it))
        }
    }

    @Test
    fun `lecture detail retains the referrer key when absent`() {
        assertEquals(
            mapOf("lecture_id" to "lecture-42", "referrer" to null),
            AnalyticsScreen.LectureDetail(LectureDetailParameter("lecture-42", null)).getExtraParameters()!!.values(),
        )
    }

    @Test
    fun `review detail retains lecture ID and referrer`() {
        assertEquals(
            mapOf("lecture_id" to "lecture-42", "referrer" to "lectureList"),
            AnalyticsScreen.ReviewDetail(
                ReviewDetailParameter("lecture-42", DetailScreenReferrer.LectureList),
            ).getExtraParameters()!!.values(),
        )
    }

    @Test
    fun `syllabus retains only the lecture ID`() {
        assertEquals(
            mapOf("lecture_id" to "lecture-42"),
            AnalyticsScreen.LectureSyllabus(LectureSyllabusParameter("lecture-42")).getExtraParameters()!!.values(),
        )
    }

    private fun Bundle.values(): Map<String, String?> = keySet().associateWith { getString(it) }
}
