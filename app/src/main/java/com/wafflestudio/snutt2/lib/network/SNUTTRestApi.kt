package com.wafflestudio.snutt2.lib.network

import com.wafflestudio.snutt2.lib.network.dto.BuildingsResponse
import com.wafflestudio.snutt2.lib.network.dto.DeleteFirebaseTokenResults
import com.wafflestudio.snutt2.lib.network.dto.DeleteLectureResults
import com.wafflestudio.snutt2.lib.network.dto.DeleteSocialLinkResults
import com.wafflestudio.snutt2.lib.network.dto.DeleteTableResults
import com.wafflestudio.snutt2.lib.network.dto.DeleteUserAccountResults
import com.wafflestudio.snutt2.lib.network.dto.DiaryQuestionnaireRequestDto
import com.wafflestudio.snutt2.lib.network.dto.DiarySubmissionRequestDto
import com.wafflestudio.snutt2.lib.network.dto.GetBookmarkListResults
import com.wafflestudio.snutt2.lib.network.dto.GetCoursebookResults
import com.wafflestudio.snutt2.lib.network.dto.GetCoursebooksOfficialResults
import com.wafflestudio.snutt2.lib.network.dto.GetDailyClassTypesResults
import com.wafflestudio.snutt2.lib.network.dto.GetFriendCourseBooksResults
import com.wafflestudio.snutt2.lib.network.dto.GetFriendPrimaryTableResults
import com.wafflestudio.snutt2.lib.network.dto.GetFriendRequestLinkResults
import com.wafflestudio.snutt2.lib.network.dto.GetFriendsResults
import com.wafflestudio.snutt2.lib.network.dto.GetLectureReviewSummaryResult
import com.wafflestudio.snutt2.lib.network.dto.GetMyDiarySubmissionsResults
import com.wafflestudio.snutt2.lib.network.dto.GetNotificationCountResults
import com.wafflestudio.snutt2.lib.network.dto.GetNotificationResults
import com.wafflestudio.snutt2.lib.network.dto.GetPopupResults
import com.wafflestudio.snutt2.lib.network.dto.GetRecentTableResults
import com.wafflestudio.snutt2.lib.network.dto.GetRemoteConfigResponse
import com.wafflestudio.snutt2.lib.network.dto.GetSemesterStatusResult
import com.wafflestudio.snutt2.lib.network.dto.GetSocialProvidersResults
import com.wafflestudio.snutt2.lib.network.dto.GetTableByIdResults
import com.wafflestudio.snutt2.lib.network.dto.GetTableListResults
import com.wafflestudio.snutt2.lib.network.dto.GetTagListResults
import com.wafflestudio.snutt2.lib.network.dto.GetThemesResults
import com.wafflestudio.snutt2.lib.network.dto.GetTimetableRemindersResults
import com.wafflestudio.snutt2.lib.network.dto.GetUserInfoResults
import com.wafflestudio.snutt2.lib.network.dto.GetVacancyLecturesResults
import com.wafflestudio.snutt2.lib.network.dto.OkResponseDto
import com.wafflestudio.snutt2.lib.network.dto.PatchFriendDisplayNameParams
import com.wafflestudio.snutt2.lib.network.dto.PatchThemeParams
import com.wafflestudio.snutt2.lib.network.dto.PatchThemeResults
import com.wafflestudio.snutt2.lib.network.dto.PatchUserInfoParams
import com.wafflestudio.snutt2.lib.network.dto.PatchUserInfoResults
import com.wafflestudio.snutt2.lib.network.dto.PostAcceptFriendByLinkResults
import com.wafflestudio.snutt2.lib.network.dto.PostBookmarkParams
import com.wafflestudio.snutt2.lib.network.dto.PostCheckEmailByIdParams
import com.wafflestudio.snutt2.lib.network.dto.PostCheckEmailByIdResults
import com.wafflestudio.snutt2.lib.network.dto.PostCopyTableResults
import com.wafflestudio.snutt2.lib.network.dto.PostCopyThemeResults
import com.wafflestudio.snutt2.lib.network.dto.PostCustomLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PostCustomLectureResults
import com.wafflestudio.snutt2.lib.network.dto.PostFeedbackParams
import com.wafflestudio.snutt2.lib.network.dto.PostFeedbackResults
import com.wafflestudio.snutt2.lib.network.dto.PostFindIdParams
import com.wafflestudio.snutt2.lib.network.dto.PostFindIdResults
import com.wafflestudio.snutt2.lib.network.dto.PostForceLogoutParams
import com.wafflestudio.snutt2.lib.network.dto.PostForceLogoutResults
import com.wafflestudio.snutt2.lib.network.dto.PostLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PostRequestFriendParams
import com.wafflestudio.snutt2.lib.network.dto.PostResetPasswordParams
import com.wafflestudio.snutt2.lib.network.dto.PostSearchQueryParams
import com.wafflestudio.snutt2.lib.network.dto.PostSearchQueryResults
import com.wafflestudio.snutt2.lib.network.dto.PostSendCodeToEmailParams
import com.wafflestudio.snutt2.lib.network.dto.PostSendPwResetCodeParams
import com.wafflestudio.snutt2.lib.network.dto.PostSignInParams
import com.wafflestudio.snutt2.lib.network.dto.PostSignInResults
import com.wafflestudio.snutt2.lib.network.dto.PostSignUpParams
import com.wafflestudio.snutt2.lib.network.dto.PostSignUpResults
import com.wafflestudio.snutt2.lib.network.dto.PostSocialLinkResults
import com.wafflestudio.snutt2.lib.network.dto.PostSocialLoginParams
import com.wafflestudio.snutt2.lib.network.dto.PostSocialLoginResults
import com.wafflestudio.snutt2.lib.network.dto.PostTableParams
import com.wafflestudio.snutt2.lib.network.dto.PostTableResults
import com.wafflestudio.snutt2.lib.network.dto.PostThemeParams
import com.wafflestudio.snutt2.lib.network.dto.PostThemeResults
import com.wafflestudio.snutt2.lib.network.dto.PostUserPasswordParams
import com.wafflestudio.snutt2.lib.network.dto.PostUserPasswordResults
import com.wafflestudio.snutt2.lib.network.dto.PostVerifyEmailCodeParams
import com.wafflestudio.snutt2.lib.network.dto.PostVerifyPwResetCodeParams
import com.wafflestudio.snutt2.lib.network.dto.PushPreferenceDto
import com.wafflestudio.snutt2.lib.network.dto.PutLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PutLectureResults
import com.wafflestudio.snutt2.lib.network.dto.PutTableParams
import com.wafflestudio.snutt2.lib.network.dto.PutTableResults
import com.wafflestudio.snutt2.lib.network.dto.PutTableThemeParams
import com.wafflestudio.snutt2.lib.network.dto.PutTableThemeResult
import com.wafflestudio.snutt2.lib.network.dto.PutTimetableLectureReminderParams
import com.wafflestudio.snutt2.lib.network.dto.PutUserPasswordParams
import com.wafflestudio.snutt2.lib.network.dto.PutUserPasswordResults
import com.wafflestudio.snutt2.lib.network.dto.RegisterFirebaseTokenParams
import com.wafflestudio.snutt2.lib.network.dto.RegisterFirebaseTokenResults
import com.wafflestudio.snutt2.lib.network.dto.ResetLectureResults
import com.wafflestudio.snutt2.lib.network.dto.core.DiaryQuestionnaireDto
import com.wafflestudio.snutt2.lib.network.dto.core.TimetableLectureReminderDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by makesource on 2016. 1. 16..
 */
interface SNUTTRestApi {
    // API Basics and Auth
    @GET("/v1/notification")
    suspend fun _getNotification(
        @Query(value = "limit") limit: Int,
        @Query(value = "offset") offset: Int,
        @Query(value = "explicit") explicit: Int,
    ): GetNotificationResults

    @GET("/v1/notification/count")
    suspend fun _getNotificationCount(): GetNotificationCountResults

    @GET("/v1/tags/{year}/{semester}")
    suspend fun _getTagList(
        @Path("year") year: Int,
        @Path("semester") semester: Int,
    ): GetTagListResults

    @POST("/v1/search_query")
    suspend fun _postSearchQuery(
        @Body body: PostSearchQueryParams,
    ): PostSearchQueryResults

    @GET("/v1/course_books")
    suspend fun _getCoursebook(): GetCoursebookResults

    // API Timetable
    @GET("/v1/tables")
    suspend fun _getTableList(): GetTableListResults

    @POST("/v1/tables")
    suspend fun _postTable(
        @Body body: PostTableParams,
    ): PostTableResults

    @GET("/v1/tables/{id}")
    suspend fun _getTableById(
        @Path("id") id: String,
    ): GetTableByIdResults

    @GET("/v1/tables/recent")
    suspend fun _getRecentTable(): GetRecentTableResults

    @DELETE("/v1/tables/{id}")
    suspend fun _deleteTable(
        @Path("id") id: String,
    ): DeleteTableResults

    @PUT("/v1/tables/{id}")
    suspend fun _putTable(
        @Path("id") id: String,
        @Body body: PutTableParams,
    ): PutTableResults

    @PUT("/v1/tables/{id}/theme")
    suspend fun _putTableTheme(
        @Path("id") id: String,
        @Body body: PutTableThemeParams,
    ): PutTableThemeResult

    @POST("/v1/tables/{id}/copy")
    suspend fun _copyTable(
        @Path("id") id: String,
    ): PostCopyTableResults

    @POST("/v1/tables/{id}/lecture")
    suspend fun _postCustomLecture(
        @Path("id") id: String,
        @Body body: PostCustomLectureParams,
    ): PostCustomLectureResults

    @POST("/v1/tables/{id}/lecture/{lecture_id}")
    suspend fun _postAddLecture(
        @Path("id") id: String,
        @Path("lecture_id") lecture_id: String,
        @Body is_forced: PostLectureParams,
    ): PostCustomLectureResults

    @DELETE("/v1/tables/{id}/lecture/{lecture_id}")
    suspend fun _deleteLecture(
        @Path("id") id: String,
        @Path("lecture_id") lecture_id: String,
    ): DeleteLectureResults

    @PUT("/v1/tables/{id}/lecture/{lecture_id}")
    suspend fun _putLecture(
        @Path("id") id: String,
        @Path("lecture_id") lecture_id: String,
        @Body body: PutLectureParams,
    ): PutLectureResults

    @PUT("/v1/tables/{id}/lecture/{lecture_id}/reset")
    suspend fun _resetLecture(
        @Path("id") id: String,
        @Path("lecture_id") lecture_id: String,
    ): ResetLectureResults

    @GET("/v1/tables/{timetableId}/lecture/reminders")
    suspend fun _getTimetableReminders(
        @Path("timetableId") timetableId: String,
    ): GetTimetableRemindersResults

    @GET("/v1/tables/{timetableId}/lecture/{timetableLectureId}/reminder")
    suspend fun _getTimetableLectureReminder(
        @Path("timetableId") timetableId: String,
        @Path("timetableLectureId") timetableLectureId: String,
    ): TimetableLectureReminderDto

    @PUT("/v1/tables/{timetableId}/lecture/{timetableLectureId}/reminder")
    suspend fun _putTimetableLectureReminder(
        @Path("timetableId") timetableId: String,
        @Path("timetableLectureId") timetableLectureId: String,
        @Body body: PutTimetableLectureReminderParams,
    ): TimetableLectureReminderDto

    @DELETE("/v1/tables/{timetableId}/lecture/{timetableLectureId}/reminder")
    suspend fun _deleteTimetableLectureReminder(
        @Path("timetableId") timetableId: String,
        @Path("timetableLectureId") timetableLectureId: String,
    )

    @GET("/v1/course_books/official")
    suspend fun _getCoursebooksOfficial(
        @Query(value = "year") year: Long,
        @Query(value = "semester") semester: Long,
        @Query(value = "course_number") courseNumber: String,
        @Query(value = "lecture_number") lectureNumber: String,
    ): GetCoursebooksOfficialResults

    @POST("/v1/auth/register_local")
    suspend fun _postSignUp(
        @Body body: PostSignUpParams,
    ): PostSignUpResults

    @POST("/v1/auth/login_local")
    suspend fun _postSignIn(
        @Body body: PostSignInParams,
    ): PostSignInResults

    @POST("/v1/auth/logout")
    suspend fun _postForceLogout(
        @Body body: PostForceLogoutParams,
    ): PostForceLogoutResults

    @POST("/v1/auth/id/find")
    suspend fun _postFindId(
        @Body body: PostFindIdParams,
    ): PostFindIdResults

    @POST("/v1/auth/password/reset/email/check")
    suspend fun _postCheckEmailById(
        @Body body: PostCheckEmailByIdParams,
    ): PostCheckEmailByIdResults

    @POST("/v1/auth/password/reset/email/send")
    suspend fun _postSendPwResetCodeToEmailById(
        @Body body: PostSendPwResetCodeParams,
    )

    @POST("/v1/auth/password/reset/verification/code")
    suspend fun _postVerifyCodeToResetPassword(
        @Body body: PostVerifyPwResetCodeParams,
    )

    @POST("/v1/auth/password/reset")
    suspend fun _postResetPassword(
        @Body body: PostResetPasswordParams,
    )

    @POST("/v1/user/email/verification")
    suspend fun _postSendCodeToEmail(
        @Body body: PostSendCodeToEmailParams,
    )

    @POST("/v1/user/email/verification/code")
    suspend fun _postVerifyEmailCode(
        @Body body: PostVerifyEmailCodeParams,
    )

    @GET("/v1/users/me")
    suspend fun _getUserInfo(): GetUserInfoResults

    @PATCH("/v1/users/me")
    suspend fun _patchUserInfo(
        @Body body: PatchUserInfoParams,
    ): PatchUserInfoResults

    @PUT("/v1/user/password")
    suspend fun _putUserPassword(
        @Body body: PutUserPasswordParams,
    ): PutUserPasswordResults

    @POST("/v1/user/password")
    suspend fun _postUserPassword(
        @Body body: PostUserPasswordParams,
    ): PostUserPasswordResults

    @POST("/v1/user/device/{registration_id}")
    suspend fun _registerFirebaseToken(
        @Path("registration_id") id: String,
        @Body body: RegisterFirebaseTokenParams,
    ): RegisterFirebaseTokenResults

    @DELETE("/v1/user/device/{registration_id}")
    suspend fun _deleteFirebaseToken(
        @Path("registration_id") id: String,
    ): DeleteFirebaseTokenResults

    @DELETE("/v1/user/account")
    suspend fun _deleteUserAccount(): DeleteUserAccountResults

    @POST("/v1/feedback")
    suspend fun _postFeedback(
        @Body body: PostFeedbackParams,
    ): PostFeedbackResults

    @GET("/v1/popups")
    suspend fun _getPopup(): GetPopupResults

    @GET("/v1/bookmarks")
    suspend fun _getBookmarkList(
        @Query(value = "year") year: Long,
        @Query(value = "semester") semester: Long,
    ): GetBookmarkListResults

    @POST("/v1/bookmarks/lecture")
    suspend fun _addBookmark(
        @Body body: PostBookmarkParams,
    )

    @HTTP(method = "DELETE", path = "/v1/bookmarks/lecture", hasBody = true)
    suspend fun _deleteBookmark(
        @Body body: PostBookmarkParams,
    )

    @GET("/v1/vacancy-notifications/lectures")
    suspend fun _getVacancyLectures(): GetVacancyLecturesResults

    @POST("/v1/vacancy-notifications/lectures/{lectureId}")
    suspend fun _postVacancyLecture(
        @Path("lectureId") lectureId: String,
    )

    @DELETE("/v1/vacancy-notifications/lectures/{lectureId}")
    suspend fun _deleteVacancyLecture(
        @Path("lectureId") lectureId: String,
    )

    @GET("/v1/configs")
    suspend fun _getRemoteConfig(): GetRemoteConfigResponse

    @POST("/v1/tables/{id}/primary")
    suspend fun _postPrimaryTable(
        @Path("id") tableId: String,
    )

    @DELETE("/v1/tables/{id}/primary")
    suspend fun _deletePrimaryTable(
        @Path("id") tableId: String,
    )

    @GET("/v1/themes")
    suspend fun _getThemes(): GetThemesResults

    @POST("/v1/themes")
    suspend fun _postTheme(
        @Body body: PostThemeParams,
    ): PostThemeResults

    @DELETE("/v1/themes/{themeId}")
    suspend fun _deleteTheme(
        @Path("themeId") themeId: String,
    )

    @PATCH("/v1/themes/{themeId}")
    suspend fun _patchTheme(
        @Path("themeId") themeId: String,
        @Body patchThemeParams: PatchThemeParams,
    ): PatchThemeResults

    @POST("/v1/themes/{themeId}/copy")
    suspend fun _postCopyTheme(
        @Path("themeId") themeId: String,
    ): PostCopyThemeResults

    @GET("/v1/buildings")
    suspend fun _getBuildings(
        @Query("places") places: String,
    ): BuildingsResponse

    @GET("/v1/ev/lectures/{lectureId}/summary")
    suspend fun _getLectureReviewSummary(
        @Path("lectureId") lectureId: String,
    ): GetLectureReviewSummaryResult

    @GET("/v1/push/preferences")
    suspend fun _getPushPreferences(): PushPreferenceDto

    @POST("/v1/push/preferences")
    suspend fun _postPushPreferences(
        @Body pushPreferences: PushPreferenceDto,
    )

    @GET("/v1/semesters/status")
    suspend fun _getSemesterStatus(): GetSemesterStatusResult

    /**
     * 소셜 로그인 관련.
     *
     * POST /auth/login: 로그인
     *
     * POST /user: 연동
     *
     * DELETE /user: 연동 해제
     */
    @GET("/v1/users/me/social_providers")
    suspend fun _getSocialProviders(): GetSocialProvidersResults

    @POST("/v1/auth/login/facebook")
    suspend fun _postLoginFacebook(
        @Body body: PostSocialLoginParams,
    ): PostSocialLoginResults

    @POST("/v1/user/facebook")
    suspend fun _postUserFacebook(
        @Body body: PostSocialLoginParams,
    ): PostSocialLinkResults

    @DELETE("/v1/user/facebook")
    suspend fun _deleteUserFacebook(): DeleteSocialLinkResults

    @POST("/v1/auth/login/google")
    suspend fun _postLoginGoogle(
        @Body body: PostSocialLoginParams,
    ): PostSocialLoginResults

    @POST("/v1/user/google")
    suspend fun _postUserGoogle(
        @Body body: PostSocialLoginParams,
    ): PostSocialLinkResults

    @DELETE("/v1/user/google")
    suspend fun _deleteUserGoogle(): DeleteSocialLinkResults

    @POST("/v1/auth/login/kakao")
    suspend fun _postLoginKakao(
        @Body body: PostSocialLoginParams,
    ): PostSocialLoginResults

    @POST("/v1/user/kakao")
    suspend fun _postUserKakao(
        @Body body: PostSocialLoginParams,
    ): PostSocialLinkResults

    @DELETE("/v1/user/kakao")
    suspend fun _deleteUserKakao(): DeleteSocialLinkResults

    // Friend APIs
    @GET("/v1/friends")
    suspend fun _getFriends(
        @Query("state") state: String,
    ): GetFriendsResults

    @POST("/v1/friends")
    suspend fun _requestFriend(
        @Body body: PostRequestFriendParams,
    ) // : GetFriendsResults FIXME 질문해놓음

    @POST("/v1/friends/accept-link/{requestToken}")
    suspend fun _acceptFriendByLink(
        @Path("requestToken") requestToken: String,
    ): PostAcceptFriendByLinkResults

    @GET("/v1/friends/generate-link")
    suspend fun _generateFriendLink(): GetFriendRequestLinkResults

    @DELETE("/v1/friends/{friendId}")
    suspend fun _deleteFriend(
        @Path("friendId") friendId: String,
    )

    @POST("/v1/friends/{friendId}/accept")
    suspend fun _acceptFriend(
        @Path("friendId") friendId: String,
    )

    @POST("/v1/friends/{friendId}/decline")
    suspend fun _declineFriend(
        @Path("friendId") friendId: String,
    )

    @GET("/v1/friends/{friendId}/coursebooks")
    suspend fun _getFriendCourseBooks(
        @Path("friendId") friendId: String,
    ): GetFriendCourseBooksResults

    @GET("/v1/friends/{friendId}/primary-table")
    suspend fun _getFriendPrimaryTable(
        @Path("friendId") friendId: String,
        @Query("semester") semester: String,
        @Query("year") year: Int,
    ): GetFriendPrimaryTableResults

    @PATCH("/v1/friends/{friendId}/display-name")
    suspend fun _patchFriendDisplayName(
        @Path("friendId") friendId: String,
        @Body body: PatchFriendDisplayNameParams,
    )

    @POST("/v1/diary")
    suspend fun _submitDiary(
        @Body body: DiarySubmissionRequestDto,
    ): OkResponseDto

    @GET("/v1/diary/dailyClassTypes")
    suspend fun _getDailyClassTypes(): GetDailyClassTypesResults

    @GET("/v1/diary/my")
    suspend fun _getMyDiarySubmissions(): GetMyDiarySubmissionsResults

    @POST("/v1/diary/questionnaire")
    suspend fun _getQuestionnaireFromActivities(
        @Body body: DiaryQuestionnaireRequestDto,
    ): DiaryQuestionnaireDto

    @DELETE("/v1/diary/{id}")
    suspend fun _removeDiarySubmission(
        @Path("id") id: String,
    ): OkResponseDto
}
