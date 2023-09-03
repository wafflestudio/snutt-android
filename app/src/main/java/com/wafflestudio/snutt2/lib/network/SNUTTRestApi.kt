package com.wafflestudio.snutt2.lib.network

import com.wafflestudio.snutt2.lib.network.dto.*
import retrofit2.http.*

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

    @GET("/v1/user/info")
    suspend fun _getUserInfo(): GetUserInfoResults

    @PUT("/v1/user/info")
    suspend fun _putUserInfo(
        @Body body: PutUserInfoParams,
    ): PutUserInfoResults

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
        @Path("id") tableId: String
    )

    @DELETE("/v1/tables/{id}/primary")
    suspend fun _deletePrimaryTable(
        @Path("id") tableId: String
    )
}
