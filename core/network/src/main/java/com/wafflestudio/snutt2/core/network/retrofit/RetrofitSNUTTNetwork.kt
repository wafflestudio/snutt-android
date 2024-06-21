package com.wafflestudio.snutt2.core.network.retrofit

import com.wafflestudio.snutt2.core.network.SNUTTNetworkDataSource
import com.wafflestudio.snutt2.core.network.model.DeleteFirebaseTokenResults
import com.wafflestudio.snutt2.core.network.model.DeleteLectureResults
import com.wafflestudio.snutt2.core.network.model.DeleteTableResults
import com.wafflestudio.snutt2.core.network.model.DeleteUserAccountResults
import com.wafflestudio.snutt2.core.network.model.DeleteUserFacebookResults
import com.wafflestudio.snutt2.core.network.model.GetBookmarkListResults
import com.wafflestudio.snutt2.core.network.model.GetCoursebookResults
import com.wafflestudio.snutt2.core.network.model.GetCoursebooksOfficialResults
import com.wafflestudio.snutt2.core.network.model.GetLecturesIdResults
import com.wafflestudio.snutt2.core.network.model.GetNotificationCountResults
import com.wafflestudio.snutt2.core.network.model.GetNotificationResults
import com.wafflestudio.snutt2.core.network.model.GetPopupResults
import com.wafflestudio.snutt2.core.network.model.GetRecentTableResults
import com.wafflestudio.snutt2.core.network.model.GetRemoteConfigResponse
import com.wafflestudio.snutt2.core.network.model.GetTableByIdResults
import com.wafflestudio.snutt2.core.network.model.GetTableListResults
import com.wafflestudio.snutt2.core.network.model.GetTagListResults
import com.wafflestudio.snutt2.core.network.model.GetThemesResults
import com.wafflestudio.snutt2.core.network.model.GetUserFacebookResults
import com.wafflestudio.snutt2.core.network.model.GetUserInfoResults
import com.wafflestudio.snutt2.core.network.model.GetVacancyLecturesResults
import com.wafflestudio.snutt2.core.network.model.PatchThemeParams
import com.wafflestudio.snutt2.core.network.model.PatchThemeResults
import com.wafflestudio.snutt2.core.network.model.PatchUserInfoParams
import com.wafflestudio.snutt2.core.network.model.PatchUserInfoResults
import com.wafflestudio.snutt2.core.network.model.PostBookmarkParams
import com.wafflestudio.snutt2.core.network.model.PostCheckEmailByIdParams
import com.wafflestudio.snutt2.core.network.model.PostCheckEmailByIdResults
import com.wafflestudio.snutt2.core.network.model.PostCopyTableResults
import com.wafflestudio.snutt2.core.network.model.PostCopyThemeResults
import com.wafflestudio.snutt2.core.network.model.PostCustomLectureParams
import com.wafflestudio.snutt2.core.network.model.PostCustomLectureResults
import com.wafflestudio.snutt2.core.network.model.PostFeedbackParams
import com.wafflestudio.snutt2.core.network.model.PostFeedbackResults
import com.wafflestudio.snutt2.core.network.model.PostFindIdParams
import com.wafflestudio.snutt2.core.network.model.PostFindIdResults
import com.wafflestudio.snutt2.core.network.model.PostForceLogoutParams
import com.wafflestudio.snutt2.core.network.model.PostForceLogoutResults
import com.wafflestudio.snutt2.core.network.model.PostLectureParams
import com.wafflestudio.snutt2.core.network.model.PostLoginFacebookParams
import com.wafflestudio.snutt2.core.network.model.PostLoginFacebookResults
import com.wafflestudio.snutt2.core.network.model.PostResetPasswordParams
import com.wafflestudio.snutt2.core.network.model.PostSearchQueryParams
import com.wafflestudio.snutt2.core.network.model.PostSearchQueryResults
import com.wafflestudio.snutt2.core.network.model.PostSendCodeToEmailParams
import com.wafflestudio.snutt2.core.network.model.PostSendPwResetCodeParams
import com.wafflestudio.snutt2.core.network.model.PostSignInParams
import com.wafflestudio.snutt2.core.network.model.PostSignInResults
import com.wafflestudio.snutt2.core.network.model.PostSignUpParams
import com.wafflestudio.snutt2.core.network.model.PostSignUpResults
import com.wafflestudio.snutt2.core.network.model.PostTableParams
import com.wafflestudio.snutt2.core.network.model.PostTableResults
import com.wafflestudio.snutt2.core.network.model.PostThemeParams
import com.wafflestudio.snutt2.core.network.model.PostThemeResults
import com.wafflestudio.snutt2.core.network.model.PostUserFacebookParams
import com.wafflestudio.snutt2.core.network.model.PostUserFacebookResults
import com.wafflestudio.snutt2.core.network.model.PostUserPasswordParams
import com.wafflestudio.snutt2.core.network.model.PostUserPasswordResults
import com.wafflestudio.snutt2.core.network.model.PostVerifyEmailCodeParams
import com.wafflestudio.snutt2.core.network.model.PostVerifyPwResetCodeParams
import com.wafflestudio.snutt2.core.network.model.PutLectureParams
import com.wafflestudio.snutt2.core.network.model.PutLectureResults
import com.wafflestudio.snutt2.core.network.model.PutTableParams
import com.wafflestudio.snutt2.core.network.model.PutTableResults
import com.wafflestudio.snutt2.core.network.model.PutTableThemeParams
import com.wafflestudio.snutt2.core.network.model.PutTableThemeResult
import com.wafflestudio.snutt2.core.network.model.PutUserPasswordParams
import com.wafflestudio.snutt2.core.network.model.PutUserPasswordResults
import com.wafflestudio.snutt2.core.network.model.RegisterFirebaseTokenParams
import com.wafflestudio.snutt2.core.network.model.RegisterFirebaseTokenResults
import com.wafflestudio.snutt2.core.network.model.ResetLectureResults
import com.wafflestudio.snutt2.core.network.model.BuildingsResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

interface RetrofitSNUTTNetworkApi{
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

class RetrofitSNUTTNetwork @Inject constructor(
    private val networkApi: RetrofitSNUTTNetworkApi
): SNUTTNetworkDataSource{
    override suspend fun _getNotification(
        limit: Int,
        offset: Int,
        explicit: Int
    ): GetNotificationResults = networkApi._getNotification(limit = limit, offset = offset, explicit = explicit)

    override suspend fun _getNotificationCount(): GetNotificationCountResults {
        TODO("Not yet implemented")
    }

    override suspend fun _getTagList(year: Int, semester: Int): GetTagListResults {
        TODO("Not yet implemented")
    }

    override suspend fun _postSearchQuery(body: PostSearchQueryParams): PostSearchQueryResults {
        TODO("Not yet implemented")
    }

    override suspend fun _getCoursebook(): GetCoursebookResults {
        TODO("Not yet implemented")
    }

    override suspend fun _getTableList(): GetTableListResults {
        TODO("Not yet implemented")
    }

    override suspend fun _postTable(body: PostTableParams): PostTableResults {
        TODO("Not yet implemented")
    }

    override suspend fun _getTableById(id: String): GetTableByIdResults {
        TODO("Not yet implemented")
    }

    override suspend fun _getRecentTable(): GetRecentTableResults {
        TODO("Not yet implemented")
    }

    override suspend fun _deleteTable(id: String): DeleteTableResults {
        TODO("Not yet implemented")
    }

    override suspend fun _putTable(id: String, body: PutTableParams): PutTableResults {
        TODO("Not yet implemented")
    }

    override suspend fun _putTableTheme(
        id: String,
        body: PutTableThemeParams
    ): PutTableThemeResult {
        TODO("Not yet implemented")
    }

    override suspend fun _copyTable(id: String): PostCopyTableResults {
        TODO("Not yet implemented")
    }

    override suspend fun _postCustomLecture(
        id: String,
        body: PostCustomLectureParams
    ): PostCustomLectureResults {
        TODO("Not yet implemented")
    }

    override suspend fun _postAddLecture(
        id: String,
        lecture_id: String,
        is_forced: PostLectureParams
    ): PostCustomLectureResults {
        TODO("Not yet implemented")
    }

    override suspend fun _deleteLecture(id: String, lecture_id: String): DeleteLectureResults {
        TODO("Not yet implemented")
    }

    override suspend fun _putLecture(
        id: String,
        lecture_id: String,
        body: PutLectureParams
    ): PutLectureResults {
        TODO("Not yet implemented")
    }

    override suspend fun _resetLecture(id: String, lecture_id: String): ResetLectureResults {
        TODO("Not yet implemented")
    }

    override suspend fun _getCoursebooksOfficial(
        year: Long,
        semester: Long,
        courseNumber: String,
        lectureNumber: String
    ): GetCoursebooksOfficialResults {
        TODO("Not yet implemented")
    }

    override suspend fun _postSignUp(body: PostSignUpParams): PostSignUpResults {
        TODO("Not yet implemented")
    }

    override suspend fun _postSignIn(body: PostSignInParams): PostSignInResults {
        TODO("Not yet implemented")
    }

    override suspend fun _postLoginFacebook(body: PostLoginFacebookParams): PostLoginFacebookResults {
        TODO("Not yet implemented")
    }

    override suspend fun _postForceLogout(body: PostForceLogoutParams): PostForceLogoutResults {
        TODO("Not yet implemented")
    }

    override suspend fun _postFindId(body: PostFindIdParams): PostFindIdResults {
        TODO("Not yet implemented")
    }

    override suspend fun _postCheckEmailById(body: PostCheckEmailByIdParams): PostCheckEmailByIdResults {
        TODO("Not yet implemented")
    }

    override suspend fun _postSendPwResetCodeToEmailById(body: PostSendPwResetCodeParams) {
        TODO("Not yet implemented")
    }

    override suspend fun _postVerifyCodeToResetPassword(body: PostVerifyPwResetCodeParams) {
        TODO("Not yet implemented")
    }

    override suspend fun _postResetPassword(body: PostResetPasswordParams) {
        TODO("Not yet implemented")
    }

    override suspend fun _postSendCodeToEmail(body: PostSendCodeToEmailParams) {
        TODO("Not yet implemented")
    }

    override suspend fun _postVerifyEmailCode(body: PostVerifyEmailCodeParams) {
        TODO("Not yet implemented")
    }

    override suspend fun _getUserInfo(): GetUserInfoResults {
        TODO("Not yet implemented")
    }

    override suspend fun _patchUserInfo(body: PatchUserInfoParams): PatchUserInfoResults {
        TODO("Not yet implemented")
    }

    override suspend fun _putUserPassword(body: PutUserPasswordParams): PutUserPasswordResults {
        TODO("Not yet implemented")
    }

    override suspend fun _postUserPassword(body: PostUserPasswordParams): PostUserPasswordResults {
        TODO("Not yet implemented")
    }

    override suspend fun _postUserFacebook(body: PostUserFacebookParams): PostUserFacebookResults {
        TODO("Not yet implemented")
    }

    override suspend fun _deleteUserFacebook(): DeleteUserFacebookResults {
        TODO("Not yet implemented")
    }

    override suspend fun _getUserFacebook(): GetUserFacebookResults {
        TODO("Not yet implemented")
    }

    override suspend fun _registerFirebaseToken(
        id: String,
        body: RegisterFirebaseTokenParams
    ): RegisterFirebaseTokenResults {
        TODO("Not yet implemented")
    }

    override suspend fun _deleteFirebaseToken(id: String): DeleteFirebaseTokenResults {
        TODO("Not yet implemented")
    }

    override suspend fun _deleteUserAccount(): DeleteUserAccountResults {
        TODO("Not yet implemented")
    }

    override suspend fun _postFeedback(body: PostFeedbackParams): PostFeedbackResults {
        TODO("Not yet implemented")
    }

    override suspend fun _getLecturesId(
        courseNumber: String,
        instructor: String
    ): GetLecturesIdResults {
        TODO("Not yet implemented")
    }

    override suspend fun _getPopup(): GetPopupResults {
        TODO("Not yet implemented")
    }

    override suspend fun _getBookmarkList(year: Long, semester: Long): GetBookmarkListResults {
        TODO("Not yet implemented")
    }

    override suspend fun _addBookmark(body: PostBookmarkParams) {
        TODO("Not yet implemented")
    }

    override suspend fun _deleteBookmark(body: PostBookmarkParams) {
        TODO("Not yet implemented")
    }

    override suspend fun _getVacancyLectures(): GetVacancyLecturesResults {
        TODO("Not yet implemented")
    }

    override suspend fun _postVacancyLecture(lectureId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun _deleteVacancyLecture(lectureId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun _getRemoteConfig(): GetRemoteConfigResponse {
        TODO("Not yet implemented")
    }

    override suspend fun _postPrimaryTable(tableId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun _deletePrimaryTable(tableId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun _getThemes(): GetThemesResults {
        TODO("Not yet implemented")
    }

    override suspend fun _postTheme(body: PostThemeParams): PostThemeResults {
        TODO("Not yet implemented")
    }

    override suspend fun _deleteTheme(themeId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun _patchTheme(
        themeId: String,
        patchThemeParams: PatchThemeParams
    ): PatchThemeResults {
        TODO("Not yet implemented")
    }

    override suspend fun _postCopyTheme(themeId: String): PostCopyThemeResults {
        TODO("Not yet implemented")
    }

    override suspend fun _getBuildings(places: String): BuildingsResponse {
        TODO("Not yet implemented")
    }

}