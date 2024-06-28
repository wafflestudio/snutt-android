package com.wafflestudio.snutt2.core.network.retrofit

import com.wafflestudio.snutt2.core.network.SNUTTNetworkDataSource
import com.wafflestudio.snutt2.core.network.model.*
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

interface RetrofitSNUTTNetworkApi {
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

    @POST("/v1/auth/login_fb")
    suspend fun _postLoginFacebook(
        @Body body: PostLoginFacebookParams,
    ): PostLoginFacebookResults

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

    @POST("/v1/user/facebook")
    suspend fun _postUserFacebook(
        @Body body: PostUserFacebookParams,
    ): PostUserFacebookResults

    @DELETE("/v1/user/facebook")
    suspend fun _deleteUserFacebook(): DeleteUserFacebookResults

    @GET("/v1/user/facebook")
    suspend fun _getUserFacebook(): GetUserFacebookResults

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

    @GET("/ev-service/v1/lectures/id")
    suspend fun _getLecturesId(
        @Query(value = "course_number") courseNumber: String,
        @Query(value = "instructor") instructor: String,
    ): GetLecturesIdResults

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

    @DELETE("/v1/bookmarks/lecture")
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
}

@Singleton
class RetrofitSNUTTNetwork @Inject constructor(
    private val networkApi: RetrofitSNUTTNetworkApi,
) : SNUTTNetworkDataSource {
    override suspend fun _getNotification(
        limit: Int,
        offset: Int,
        explicit: Int,
    ): GetNotificationResults =
        networkApi._getNotification(limit = limit, offset = offset, explicit = explicit)

    override suspend fun _getNotificationCount(): GetNotificationCountResults =
        networkApi._getNotificationCount()

    override suspend fun _getTagList(
        year: Int,
        semester: Int,
    ): GetTagListResults =
        networkApi._getTagList(year = year, semester = semester)

    override suspend fun _postSearchQuery(
        body: PostSearchQueryParams,
    ): PostSearchQueryResults =
        networkApi._postSearchQuery(body = body)

    override suspend fun _getCoursebook(): GetCoursebookResults =
        networkApi._getCoursebook()

    override suspend fun _getTableList(): GetTableListResults =
        networkApi._getTableList()

    override suspend fun _postTable(
        body: PostTableParams,
    ): PostTableResults =
        networkApi._postTable(body = body)

    override suspend fun _getTableById(
        id: String,
    ): GetTableByIdResults =
        networkApi._getTableById(id = id)

    override suspend fun _getRecentTable(): GetRecentTableResults =
        networkApi._getRecentTable()

    override suspend fun _deleteTable(
        id: String,
    ): DeleteTableResults =
        networkApi._deleteTable(id = id)

    override suspend fun _putTable(
        id: String,
        body: PutTableParams,
    ): PutTableResults =
        networkApi._putTable(id = id, body = body)

    override suspend fun _putTableTheme(
        id: String,
        body: PutTableThemeParams,
    ): PutTableThemeResult =
        networkApi._putTableTheme(id = id, body = body)

    override suspend fun _copyTable(
        id: String,
    ): PostCopyTableResults =
        networkApi._copyTable(id = id)

    override suspend fun _postCustomLecture(
        id: String,
        body: PostCustomLectureParams,
    ): PostCustomLectureResults =
        networkApi._postCustomLecture(id = id, body = body)

    override suspend fun _postAddLecture(
        id: String,
        lecture_id: String,
        is_forced: PostLectureParams,
    ): PostCustomLectureResults =
        networkApi._postAddLecture(id = id, lecture_id = lecture_id, is_forced = is_forced)

    override suspend fun _deleteLecture(
        id: String,
        lecture_id: String,
    ): DeleteLectureResults =
        networkApi._deleteLecture(id = id, lecture_id = lecture_id)

    override suspend fun _putLecture(
        id: String,
        lecture_id: String,
        body: PutLectureParams,
    ): PutLectureResults =
        networkApi._putLecture(id = id, lecture_id = lecture_id, body = body)

    override suspend fun _resetLecture(
        id: String,
        lecture_id: String,
    ): ResetLectureResults =
        networkApi._resetLecture(id = id, lecture_id = lecture_id)

    override suspend fun _getCoursebooksOfficial(
        year: Long,
        semester: Long,
        courseNumber: String,
        lectureNumber: String,
    ): GetCoursebooksOfficialResults =
        networkApi._getCoursebooksOfficial(
            year = year,
            semester = semester,
            courseNumber = courseNumber,
            lectureNumber = lectureNumber,
        )

    override suspend fun _postSignUp(
        body: PostSignUpParams,
    ): PostSignUpResults =
        networkApi._postSignUp(body = body)

    override suspend fun _postSignIn(
        body: PostSignInParams,
    ): PostSignInResults =
        networkApi._postSignIn(body = body)

    override suspend fun _postLoginFacebook(
        body: PostLoginFacebookParams,
    ): PostLoginFacebookResults =
        networkApi._postLoginFacebook(body = body)

    override suspend fun _postForceLogout(
        body: PostForceLogoutParams,
    ): PostForceLogoutResults =
        networkApi._postForceLogout(body = body)

    override suspend fun _postFindId(
        body: PostFindIdParams,
    ): PostFindIdResults =
        networkApi._postFindId(body = body)

    override suspend fun _postCheckEmailById(
        body: PostCheckEmailByIdParams,
    ): PostCheckEmailByIdResults =
        networkApi._postCheckEmailById(body = body)

    override suspend fun _postSendPwResetCodeToEmailById(
        body: PostSendPwResetCodeParams,
    ) =
        networkApi._postSendPwResetCodeToEmailById(body = body)

    override suspend fun _postVerifyCodeToResetPassword(
        body: PostVerifyPwResetCodeParams,
    ) =
        networkApi._postVerifyCodeToResetPassword(body = body)

    override suspend fun _postResetPassword(
        body: PostResetPasswordParams,
    ) =
        networkApi._postResetPassword(body = body)

    override suspend fun _postSendCodeToEmail(
        body: PostSendCodeToEmailParams,
    ) =
        networkApi._postSendCodeToEmail(body = body)

    override suspend fun _postVerifyEmailCode(
        body: PostVerifyEmailCodeParams,
    ) =
        networkApi._postVerifyEmailCode(body = body)

    override suspend fun _getUserInfo(): GetUserInfoResults =
        networkApi._getUserInfo()

    override suspend fun _patchUserInfo(
        body: PatchUserInfoParams,
    ): PatchUserInfoResults =
        networkApi._patchUserInfo(body = body)

    override suspend fun _putUserPassword(
        body: PutUserPasswordParams,
    ): PutUserPasswordResults =
        networkApi._putUserPassword(body = body)

    override suspend fun _postUserPassword(
        body: PostUserPasswordParams,
    ): PostUserPasswordResults =
        networkApi._postUserPassword(body = body)

    override suspend fun _postUserFacebook(
        body: PostUserFacebookParams,
    ): PostUserFacebookResults =
        networkApi._postUserFacebook(body = body)

    override suspend fun _deleteUserFacebook(): DeleteUserFacebookResults =
        networkApi._deleteUserFacebook()

    override suspend fun _getUserFacebook(): GetUserFacebookResults =
        networkApi._getUserFacebook()

    override suspend fun _registerFirebaseToken(
        id: String,
        body: RegisterFirebaseTokenParams,
    ): RegisterFirebaseTokenResults =
        networkApi._registerFirebaseToken(id = id, body = body)

    override suspend fun _deleteFirebaseToken(
        id: String,
    ): DeleteFirebaseTokenResults =
        networkApi._deleteFirebaseToken(id = id)

    override suspend fun _deleteUserAccount(): DeleteUserAccountResults =
        networkApi._deleteUserAccount()

    override suspend fun _postFeedback(
        body: PostFeedbackParams,
    ): PostFeedbackResults =
        networkApi._postFeedback(body = body)

    override suspend fun _getLecturesId(
        courseNumber: String,
        instructor: String,
    ): GetLecturesIdResults =
        networkApi._getLecturesId(courseNumber = courseNumber, instructor = instructor)

    override suspend fun _getPopup(): GetPopupResults =
        networkApi._getPopup()

    override suspend fun _getBookmarkList(
        year: Long,
        semester: Long,
    ): GetBookmarkListResults =
        networkApi._getBookmarkList(year = year, semester = semester)

    override suspend fun _addBookmark(
        body: PostBookmarkParams,
    ) =
        networkApi._addBookmark(body = body)

    override suspend fun _deleteBookmark(
        body: PostBookmarkParams,
    ) =
        networkApi._deleteBookmark(body = body)

    override suspend fun _getVacancyLectures(): GetVacancyLecturesResults =
        networkApi._getVacancyLectures()

    override suspend fun _postVacancyLecture(
        lectureId: String,
    ) =
        networkApi._postVacancyLecture(lectureId = lectureId)

    override suspend fun _deleteVacancyLecture(
        lectureId: String,
    ) =
        networkApi._deleteVacancyLecture(lectureId = lectureId)

    override suspend fun _getRemoteConfig(): GetRemoteConfigResponse =
        networkApi._getRemoteConfig()

    override suspend fun _postPrimaryTable(
        tableId: String,
    ) =
        networkApi._postPrimaryTable(tableId = tableId)

    override suspend fun _deletePrimaryTable(
        tableId: String,
    ) =
        networkApi._deletePrimaryTable(tableId = tableId)

    override suspend fun _getThemes(): GetThemesResults =
        networkApi._getThemes()

    override suspend fun _postTheme(
        body: PostThemeParams,
    ): PostThemeResults =
        networkApi._postTheme(body = body)

    override suspend fun _deleteTheme(
        themeId: String,
    ) =
        networkApi._deleteTheme(themeId = themeId)

    override suspend fun _patchTheme(
        themeId: String,
        patchThemeParams: PatchThemeParams,
    ): PatchThemeResults =
        networkApi._patchTheme(themeId = themeId, patchThemeParams = patchThemeParams)

    override suspend fun _postCopyTheme(
        themeId: String,
    ): PostCopyThemeResults =
        networkApi._postCopyTheme(themeId = themeId)

    override suspend fun _getBuildings(
        places: String,
    ): BuildingsResponse =
        networkApi._getBuildings(places = places)
}
