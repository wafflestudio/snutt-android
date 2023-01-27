package com.wafflestudio.snutt2.lib.network

import com.wafflestudio.snutt2.lib.network.dto.*
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

/**
 * Created by makesource on 2016. 1. 16..
 */
interface SNUTTRestApi {
    // API Basics and Auth
    @POST("/auth/register_local")
    fun postSignUp(
        @Body body: PostSignUpParams
    ): Single<PostSignUpResults>

    @POST("/auth/login_local")
    fun postSignIn(
        @Body body: PostSignInParams
    ): Single<PostSignInResults>

    @POST("/auth/login_fb")
    fun postLoginFacebook(
        @Body body: PostLoginFacebookParams
    ): Single<PostLoginFacebookResults>

    @POST("/auth/logout")
    fun postForceLogout(
        @Body body: PostForceLogoutParams
    ): Single<PostForceLogoutResults>

    @POST("/search_query")
    fun postSearchQuery(
        @Body body: PostSearchQueryParams
    ): Single<PostSearchQueryResults>

    @GET("/app_version")
    fun getAppVersion(): Single<GetAppVersionResults>

    @GET("/colors/{name}")
    fun getColorList(
        @Path("name") name: String
    ): Single<GetColorListResults>

    // API Feedback
    @POST("/feedback")
    fun postFeedback(
        @Body body: PostFeedbackParams
    ): Single<PostFeedbackResults>

    // API Coursebook
    @GET("/course_books")
    fun getCoursebook(): Single<GetCoursebookResults>

    @GET("/course_books/official")
    fun getCoursebooksOfficial(
        @Query(value = "year") year: Long,
        @Query(value = "semester") semester: Long,
        @Query(value = "course_number") courseNumber: String,
        @Query(value = "lecture_number") lectureNumber: String,
    ): Single<GetCoursebooksOfficialResults>

    // API Timetable
    @GET("/tables")
    fun getTableList(): Single<GetTableListResults>

    @POST("/tables")
    fun postTable(
        @Body body: PostTableParams,
    ): Single<PostTableResults>

    @GET("/tables/{id}")
    fun getTableById(
        @Path("id") id: String,
    ): Single<GetTableByIdResults>

    @GET("/tables/recent")
    fun getRecentTable(): Single<GetRecentTableResults>

    @DELETE("/tables/{id}")
    fun deleteTable(
        @Path("id") id: String,
    ): Single<DeleteTableResults>

    @PUT("/tables/{id}")
    fun putTable(
        @Path("id") id: String,
        @Body body: PutTableParams
    ): Single<PutTableResults>

    @PUT("/tables/{id}/theme")
    fun putTableTheme(
        @Path("id") id: String,
        @Body body: PutTableThemeParams
    ): Single<PutTableThemeResult>

    @POST("/tables/{id}/copy")
    fun copyTable(
        @Path("id") id: String,
    ): Single<PostCopyTableResults>

    @POST("/tables/{id}/lecture")
    fun postCustomLecture(
        @Path("id") id: String,
        @Body body: PostCustomLectureParams,
    ): Single<PostCustomLectureResults>

    @POST("/tables/{id}/lecture/{lecture_id}")
    fun postAddLecture(
        @Path("id") id: String,
        @Path("lecture_id") lecture_id: String,
        @Body is_forced: PostLectureParams
    ): Single<PostCustomLectureResults>

    @DELETE("/tables/{id}/lecture/{lecture_id}")
    fun deleteLecture(
        @Path("id") id: String,
        @Path("lecture_id") lecture_id: String,
    ): Single<DeleteLectureResults>

    @PUT("/tables/{id}/lecture/{lecture_id}")
    fun putLecture(
        @Path("id") id: String,
        @Path("lecture_id") lecture_id: String,
        @Body body: PutLectureParams,
    ): Single<PutLectureResults>

    @PUT("/tables/{id}/lecture/{lecture_id}/reset")
    fun resetLecture(
        @Path("id") id: String,
        @Path("lecture_id") lecture_id: String,
    ): Single<ResetLectureResults>

    @GET("/tags/{year}/{semester}")
    fun getTagList(
        @Path("year") year: Int,
        @Path("semester") semester: Int,
    ): Single<GetTagListResults>

    // API for User
    @GET("/user/info")
    fun getUserInfo(): Single<GetUserInfoResults>

    @PUT("/user/info")
    fun putUserInfo(
        @Body body: PutUserInfoParams,
    ): Single<PutUserInfoResults>

    @PUT("/user/password")
    fun putUserPassword(
        @Body body: PutUserPasswordParams,
    ): Single<PutUserPasswordResults>

    @POST("/user/password")
    fun postUserPassword(
        @Body body: PostUserPasswordParams,
    ): Single<PostUserPasswordResults>

    @POST("/user/facebook")
    fun postUserFacebook(
        @Body body: PostUserFacebookParams,
    ): Single<PostUserFacebookResults>

    @DELETE("/user/facebook")
    fun deleteUserFacebook(): Single<DeleteUserFacebookResults>

    @GET("/user/facebook")
    fun getUserFacebook(): Single<GetUserFacebookResults>

    @POST("/user/device/{registration_id}")
    fun registerFirebaseToken(
        @Path("registration_id") id: String,
        @Body body: RegisterFirebaseTokenParams
    ): Single<RegisterFirebaseTokenResults>

    @DELETE("/user/device/{registration_id}")
    fun deleteFirebaseToken(
        @Path("registration_id") id: String,
    ): Single<DeleteFirebaseTokenResults>

    @DELETE("/user/account")
    fun deleteUserAccount(): Single<DeleteUserAccountResults>

    // API for Notification
    @GET("/notification")
    fun getNotification(
        @Query(value = "limit") limit: Long,
        @Query(value = "offset") offset: Long,
        @Query(value = "explicit") explicit: Long,
    ): Single<GetNotificationResults>

    @GET("/notification/count")
    fun getNotificationCount(): Single<GetNotificationCountResults>

    @GET("/ev-service/v1/lectures/id")
    fun getLecturesId(
        @Query(value = "course_number") courseNumber: String,
        @Query(value = "instructor") instructor: String
    ): Single<GetLecturesIdResults>

    @GET("/v1/popups")
    fun getPopup(): Single<GetPopupResults>

    @GET("/notification")
    suspend fun _getNotification(
        @Query(value = "limit") limit: Int,
        @Query(value = "offset") offset: Int,
        @Query(value = "explicit") explicit: Int,
    ): GetNotificationResults

    @GET("/notification/count")
    suspend fun _getNotificationCount(): GetNotificationCountResults

    @GET("/tags/{year}/{semester}")
    suspend fun _getTagList(
        @Path("year") year: Int,
        @Path("semester") semester: Int,
    ): GetTagListResults

    @POST("/search_query")
    suspend fun _postSearchQuery(
        @Body body: PostSearchQueryParams
    ): PostSearchQueryResults

    @GET("/course_books")
    suspend fun _getCoursebook(): GetCoursebookResults

    // API Timetable
    @GET("/tables")
    suspend fun _getTableList(): GetTableListResults

    @POST("/tables")
    suspend fun _postTable(
        @Body body: PostTableParams,
    ): PostTableResults

    @GET("/tables/{id}")
    suspend fun _getTableById(
        @Path("id") id: String,
    ): GetTableByIdResults

    @GET("/tables/recent")
    suspend fun _getRecentTable(): GetRecentTableResults

    @DELETE("/tables/{id}")
    suspend fun _deleteTable(
        @Path("id") id: String,
    ): DeleteTableResults

    @PUT("/tables/{id}")
    suspend fun _putTable(
        @Path("id") id: String,
        @Body body: PutTableParams
    ): PutTableResults

    @PUT("/tables/{id}/theme")
    suspend fun _putTableTheme(
        @Path("id") id: String,
        @Body body: PutTableThemeParams
    ): PutTableThemeResult

    @POST("/tables/{id}/copy")
    suspend fun _copyTable(
        @Path("id") id: String,
    ): PostCopyTableResults

    @POST("/tables/{id}/lecture")
    suspend fun _postCustomLecture(
        @Path("id") id: String,
        @Body body: PostCustomLectureParams,
    ): PostCustomLectureResults

    @POST("/tables/{id}/lecture/{lecture_id}")
    suspend fun _postAddLecture(
        @Path("id") id: String,
        @Path("lecture_id") lecture_id: String,
        @Body is_forced: PostLectureParams,
    ): PostCustomLectureResults

    @DELETE("/tables/{id}/lecture/{lecture_id}")
    suspend fun _deleteLecture(
        @Path("id") id: String,
        @Path("lecture_id") lecture_id: String,
    ): DeleteLectureResults

    @PUT("/tables/{id}/lecture/{lecture_id}")
    suspend fun _putLecture(
        @Path("id") id: String,
        @Path("lecture_id") lecture_id: String,
        @Body body: PutLectureParams
    ): PutLectureResults

    @PUT("/tables/{id}/lecture/{lecture_id}/reset")
    suspend fun _resetLecture(
        @Path("id") id: String,
        @Path("lecture_id") lecture_id: String,
    ): ResetLectureResults

    @GET("/course_books/official")
    suspend fun _getCoursebooksOfficial(
        @Query(value = "year") year: Long,
        @Query(value = "semester") semester: Long,
        @Query(value = "course_number") courseNumber: String,
        @Query(value = "lecture_number") lectureNumber: String,
    ): GetCoursebooksOfficialResults

    @POST("/auth/register_local")
    suspend fun _postSignUp(
        @Body body: PostSignUpParams
    ): PostSignUpResults

    @POST("/auth/login_local")
    suspend fun _postSignIn(
        @Body body: PostSignInParams
    ): PostSignInResults

    @POST("/auth/login_fb")
    suspend fun _postLoginFacebook(
        @Body body: PostLoginFacebookParams
    ): PostLoginFacebookResults

    @POST("/auth/logout")
    suspend fun _postForceLogout(
        @Body body: PostForceLogoutParams
    ): PostForceLogoutResults

    @POST("/v1/auth/id/find")
    suspend fun _postFindId(
        @Body body: PostFindIdParams
    ): PostFindIdResults

    @POST("/v1/auth/password/reset/email/check")
    suspend fun _postCheckEmailById(
        @Body body: PostCheckEmailByIdParams
    ): PostCheckEmailByIdResults

    @POST("/v1/auth/password/reset/email/send")
    suspend fun _postSendCodeToEmailById(
        @Body body: PostSendCodeParams
    )

    @POST("/v1/auth/password/reset/verification/code")
    suspend fun _postVerifyCodeToResetPassword(
        @Body body: PostVerifyCodeParams
    )

    @POST("/v1/auth/password/reset")
    suspend fun _postResetPassword(
        @Body body: PostResetPasswordParams
    )

    @GET("/user/info")
    suspend fun _getUserInfo(): GetUserInfoResults

    @PUT("/user/info")
    suspend fun _putUserInfo(
        @Body body: PutUserInfoParams,
    ): PutUserInfoResults

    @PUT("/user/password")
    suspend fun _putUserPassword(
        @Body body: PutUserPasswordParams,
    ): PutUserPasswordResults

    @POST("/user/password")
    suspend fun _postUserPassword(
        @Body body: PostUserPasswordParams,
    ): PostUserPasswordResults

    @POST("/user/facebook")
    suspend fun _postUserFacebook(
        @Body body: PostUserFacebookParams,
    ): PostUserFacebookResults

    @DELETE("/user/facebook")
    suspend fun _deleteUserFacebook(): DeleteUserFacebookResults

    @GET("/user/facebook")
    suspend fun _getUserFacebook(): GetUserFacebookResults

    @POST("/user/device/{registration_id}")
    suspend fun _registerFirebaseToken(
        @Path("registration_id") id: String,
        @Body body: RegisterFirebaseTokenParams
    ): RegisterFirebaseTokenResults

    @DELETE("/user/device/{registration_id}")
    suspend fun _deleteFirebaseToken(
        @Path("registration_id") id: String,
    ): DeleteFirebaseTokenResults

    @DELETE("/user/account")
    suspend fun _deleteUserAccount(): DeleteUserAccountResults

    @POST("/feedback")
    suspend fun _postFeedback(
        @Body body: PostFeedbackParams
    ): PostFeedbackResults

    @GET("/ev-service/v1/lectures/id")
    suspend fun _getLecturesId(
        @Query(value = "course_number") courseNumber: String,
        @Query(value = "instructor") instructor: String
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
        @Body body: PostBookmarkParams
    )

    @HTTP(method = "DELETE", path = "/v1/bookmarks/lecture", hasBody = true)
    suspend fun _deleteBookmark(
        @Body body: PostBookmarkParams
    )
}
