package com.wafflestudio.snutt2.logging

import org.junit.Test
import kotlin.reflect.KClass
import kotlin.test.assertEquals

/**
 * Analytics 이름은 R8 난독화에 영향받지 않도록 문자열로 명시한다. 이 테스트는 그 값이
 * 기존 BigQuery 데이터와 이어지는 레거시 이름(클래스명의 snake_case)과 같은지,
 * 그리고 새 하위 클래스가 추가될 때 여기에도 등록되었는지를 검증한다.
 */
class AnalyticsNameTest {

    private val lectureAction = LectureActionReferrer.LectureDetail
    private val detailReferrer = DetailScreenReferrer.Bookmark

    private val events: List<AnalyticsEvent> = listOf(
        AnalyticsEvent.Login(LoginParameter(LoginParameter.Provider.LOCAL)),
        AnalyticsEvent.Logout,
        AnalyticsEvent.SignUp,
        AnalyticsEvent.SearchLecture(SearchLectureParameter("q", "2024-1")),
        AnalyticsEvent.AddToBookmark(AddToBookmarkParameter("id", lectureAction)),
        AnalyticsEvent.AddToTimetable(AddToTimetableParameter("id", null, lectureAction)),
        AnalyticsEvent.AddToVacancy(AddToVacancyParameter("id", lectureAction)),
        AnalyticsEvent.DiaryFirstSectionDone,
        AnalyticsEvent.DiarySubmitted(DiarySubmittedParameter(listOf("id"), false, 0, DiaryWriteReferrer.PUSH)),
        AnalyticsEvent.DiaryWriteExit(DiaryWriteExitParameter(DiaryWriteExitParameter.Step.ACTIVITY, 0, DiaryWriteReferrer.PUSH)),
        AnalyticsEvent.PushOpen(PushOpenParameter("id", "diary")),
        AnalyticsEvent.DiaryAfterSubmit(DiaryAfterSubmitParameter(DiaryAfterSubmitParameter.Action.NEXT)),
    )

    private val screens: List<AnalyticsScreen> = listOf(
        AnalyticsScreen.TimetableHome,
        AnalyticsScreen.TimetableMenu,
        AnalyticsScreen.TimetableShare,
        AnalyticsScreen.LectureCreate,
        AnalyticsScreen.LectureDetail(LectureDetailParameter("id", null)),
        AnalyticsScreen.LectureList,
        AnalyticsScreen.LectureSyllabus(LectureSyllabusParameter("id")),
        AnalyticsScreen.SearchHome,
        AnalyticsScreen.SearchEmpty,
        AnalyticsScreen.SearchList,
        AnalyticsScreen.SearchFilter,
        AnalyticsScreen.Bookmark,
        AnalyticsScreen.ReviewHome,
        AnalyticsScreen.ReviewDetail(ReviewDetailParameter("id", detailReferrer)),
        AnalyticsScreen.Friends,
        AnalyticsScreen.SettingsHome,
        AnalyticsScreen.SettingsAccount,
        AnalyticsScreen.SettingsTimetable,
        AnalyticsScreen.SettingsColorScheme,
        AnalyticsScreen.SettingsSupport,
        AnalyticsScreen.SettingsDevelopers,
        AnalyticsScreen.ThemeHome,
        AnalyticsScreen.ThemeBasicDetail,
        AnalyticsScreen.ThemeCustomNew,
        AnalyticsScreen.ThemeCustomEdit,
        AnalyticsScreen.ThemeDownloaded,
        AnalyticsScreen.ThemePreview,
        AnalyticsScreen.DiaryWrite(DiaryWriteParameter(DiaryWriteReferrer.PUSH)),
        AnalyticsScreen.DiaryHistory,
        AnalyticsScreen.Vacancy,
        AnalyticsScreen.Popup,
        AnalyticsScreen.Login,
        AnalyticsScreen.Onboard,
    )

    @Test
    fun `이벤트 이름은 클래스명의 snake_case 와 같다`() {
        events.forEach { assertEquals(legacyName(it::class), it.name) }
    }

    @Test
    fun `화면 이름은 클래스명의 snake_case 와 같다`() {
        screens.forEach { assertEquals(legacyName(it::class), it.name) }
    }

    @Test
    fun `모든 이벤트 하위 클래스가 검증 대상에 포함된다`() {
        assertEquals(
            AnalyticsEvent::class.sealedSubclasses.toSet(),
            events.map { it::class }.toSet(),
        )
    }

    @Test
    fun `모든 화면 하위 클래스가 검증 대상에 포함된다`() {
        assertEquals(
            AnalyticsScreen::class.sealedSubclasses.toSet(),
            screens.map { it::class }.toSet(),
        )
    }

    private fun legacyName(kClass: KClass<*>): String = kClass.simpleName!!.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
}
