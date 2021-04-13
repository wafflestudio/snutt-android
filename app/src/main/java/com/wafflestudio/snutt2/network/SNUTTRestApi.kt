package com.wafflestudio.snutt2.network

import com.wafflestudio.snutt2.network.dto.*
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
        @Header("x-access-token") token: String,
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
        @Header("x-access-token") token: String
    ): Single<GetTableListResults>

    @POST("/tables")
    fun postTable(
        @Header("x-access-token") token: String,
        @Body body: PostTableParams,
    ): Single<PostTableResults>

    @GET("/tables/{id}")
    fun getTableById(
        @Header("x-access-token") token: String,
        @Path("id") id: String,
    ): Single<GetTableByIdResults>

    @GET("/tables/recent")
    fun getRecentTable(@Header("x-access-token") token: String): Single<GetRecentTableResults>

    @DELETE("/tables/{id}")
    fun deleteTable(
        @Header("x-access-token") token: String,
        @Path("id") id: String,
    ): Single<DeleteTableResults>

    @PUT("/tables/{id}")
    fun putTable(
        @Header("x-access-token") token: String,
        @Path("id") id: String,
        @Body body: PutTableParams
    ): Single<PutTableResults>

    @POST("/tables/{id}/lecture")
    fun postCustomLecture(
        @Header("x-access-token") token: String,
        @Path("id") id: String,
        @Body body: PostCustomLectureParams,
    ): Single<PostCustomLectureResults>

    @POST("/tables/{id}/lecture/{lecture_id}")
    fun postAddLecture(
        @Header("x-access-token") token: String,
        @Path("id") id: String,
        @Path("lecture_id") lecture_id: String,
    ): Single<PostCustomLectureResults>

    @DELETE("/tables/{id}/lecture/{lecture_id}")
    fun deleteLecture(
        @Header("x-access-token") token: String,
        @Path("id") id: String,
        @Path("lecture_id") lecture_id: String,
    ): Single<DeleteLectureResults>

    @PUT("/tables/{id}/lecture/{lecture_id}")
    fun putLecture(
        @Header("x-access-token") token: String,
        @Path("id") id: String,
        @Path("lecture_id") lecture_id: String,
        @Body body: PutLectureParams,
    ): Single<PutLectureResults>

    @PUT("/tables/{id}/lecture/{lecture_id}/reset")
    fun resetLecture(
        @Header("x-access-token") token: String,
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
        @Header("x-access-token") token: String
    ): Single<GetUserInfoResults>

    @PUT("/user/info")
    fun putUserInfo(
        @Header("x-access-token") token: String,
        @Body body: PutUserInfoParams,
    ): Single<PutUserInfoResults>

    @PUT("/user/password")
    fun putUserPassword(
        @Header("x-access-token") token: String,
        @Body body: PutUserPasswordParams,
    ): Single<PutUserPasswordResults>

    @POST("/user/password")
    fun postUserPassword(
        @Header("x-access-token") token: String,
        @Body body: PostUserPasswordParams,
    ): Single<PostUserPasswordResults>

    @POST("/user/facebook")
    fun postUserFacebook(
        @Header("x-access-token") token: String,
        @Body body: PostUserFacebookParams,
    ): Single<PostUserFacebookResults>

    @DELETE("/user/facebook")
    fun deleteUserFacebook(
        @Header("x-access-token") token: String
    ): Single<DeleteUserFacebookResults>

    @GET("/user/facebook")
    fun getUserFacebook(
        @Header("x-access-token") token: String
    ): Single<GetUserFacebookResults>

    @POST("/user/device/{registration_id}")
    fun registerFirebaseToken(
        @Header("x-access-token") token: String,
        @Path("registration_id") id: String,
        @Body body: RegisterFirebaseTokenParams
    ): Single<RegisterFirebaseTokenResults>

    @DELETE("/user/device/{registration_id}")
    fun deleteFirebaseToken(
        @Header("x-access-token") token: String,
        @Path("registration_id") id: String,
    ): Single<DeleteFirebaseTokenResults>

    @DELETE("/user/account")
    fun deleteUserAccount(@Header("x-access-token") token: String): Single<DeleteUserAccountResults>

    // API for Notification
    @GET("/notification")
    fun getNotification(
        @Header("x-access-token") token: String,
        @Query(value = "limit") limit: Long,
        @Query(value = "offset") offset: Long,
        @Query(value = "explicit") explicit: Long,
    ): Single<GetNotificationResults>

    @GET("/notification/count")
    fun getNotificationCount(
        @Header("x-access-token") token: String
    ): Single<GetNotificationCountResults>

}
