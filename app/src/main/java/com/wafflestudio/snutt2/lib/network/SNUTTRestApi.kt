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
    fun getTableList(
    ): Single<GetTableListResults>

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
    fun getUserInfo(
    ): Single<GetUserInfoResults>

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
    fun deleteUserFacebook(
    ): Single<DeleteUserFacebookResults>

    @GET("/user/facebook")
    fun getUserFacebook(
    ): Single<GetUserFacebookResults>

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
    fun getNotificationCount(
    ): Single<GetNotificationCountResults>
}
