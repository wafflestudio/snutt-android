package com.wafflestudio.snutt2

import com.wafflestudio.snutt2.model.*
import retrofit.Callback
import retrofit.client.Response
import retrofit.http.*

/**
 * Created by makesource on 2016. 1. 16..
 */
interface SNUTTRestApi {
    // API Basics and Auth
    @POST("/auth/register_local")
    fun postSignUp(@Body query: Map<*, *>?, callback: Callback<Token?>?)

    @POST("/auth/login_local")
    fun postSignIn(@Body query: Map<*, *>?, callback: Callback<Token?>?)

    @POST("/auth/login_fb")
    fun postLoginFacebook(@Body query: Map<*, *>?, callback: Callback<Token?>?)

    @POST("/auth/logout")
    fun postForceLogout(@Body query: Map<*, *>?, callback: Callback<Response?>?)

    @POST("/search_query")
    fun postSearchQuery(@Body query: Map<*, *>?, callback: Callback<List<Lecture?>?>?)

    @GET("/app_version")
    fun getAppVersion(callback: Callback<Version?>?)

    @GET("/colors/{name}")
    fun getColorList(@Path("name") name: String?, callback: Callback<ColorList?>?)

    // API Feedback
    @POST("/feedback")
    fun postFeedback(@Header("x-access-token") token: String?, @Body query: Map<*, *>?, callback: Callback<Response?>?)

    // API Coursebook
    @GET("/course_books")
    fun getCoursebook(callback: Callback<List<Coursebook?>?>?)

    @GET("/course_books/official")
    fun getCoursebooksOfficial(@QueryMap query: Map<*, *>?, callback: Callback<Map<*, *>?>?)

    // API Timetable
    @GET("/tables")
    fun getTableList(@Header("x-access-token") token: String?, callback: Callback<List<Table?>?>?)

    @POST("/tables")
    fun postTable(@Header("x-access-token") token: String?, @Body query: Map<*, *>?, callback: Callback<List<Table?>?>?)

    @GET("/tables/{id}")
    fun getTableById(@Header("x-access-token") token: String?, @Path("id") id: String?, callback: Callback<Table?>?)

    @GET("/tables/recent")
    fun getRecentTable(@Header("x-access-token") token: String?, callback: Callback<Table?>?)

    @DELETE("/tables/{id}")
    fun deleteTable(@Header("x-access-token") token: String?, @Path("id") id: String?, callback: Callback<List<Table?>?>?)

    @PUT("/tables/{id}")
    fun putTable(@Header("x-access-token") token: String?, @Path("id") id: String?, @Body query: Map<*, *>?, callback: Callback<List<Table?>?>?)

    @POST("/tables/{id}/lecture")
    fun postLecture(@Header("x-access-token") token: String?, @Path("id") id: String?, @Body lecture: Lecture?, callback: Callback<Table?>?)

    @POST("/tables/{id}/lecture/{lecture_id}")
    fun postLecture(@Header("x-access-token") token: String?, @Path("id") id: String?, @Path("lecture_id") lecture_id: String?, callback: Callback<Table?>?)

    @DELETE("/tables/{id}/lecture/{lecture_id}")
    fun deleteLecture(@Header("x-access-token") token: String?, @Path("id") id: String?, @Path("lecture_id") lecture_id: String?, callback: Callback<Table?>?)

    @PUT("/tables/{id}/lecture/{lecture_id}")
    fun putLecture(@Header("x-access-token") token: String?, @Path("id") id: String?, @Path("lecture_id") lecture_id: String?, @Body lecture: Lecture?, callback: Callback<Table?>?)

    @PUT("/tables/{id}/lecture/{lecture_id}/reset")
    fun resetLecture(@Header("x-access-token") token: String?, @Path("id") id: String?, @Path("lecture_id") lecture_id: String?, callback: Callback<Table?>?)

    @GET("/tags/{year}/{semester}")
    fun getTagList(@Path("year") year: Int, @Path("semester") semester: Int, callback: Callback<TagList?>?)

    // API for User
    @GET("/user/info")
    fun getUserInfo(@Header("x-access-token") token: String?, callback: Callback<User?>?)

    @PUT("/user/info")
    fun putUserInfo(@Header("x-access-token") token: String?, @Body query: Map<*, *>?, callback: Callback<Response?>?)

    @PUT("/user/password")
    fun putUserPassword(@Header("x-access-token") token: String?, @Body query: Map<*, *>?, callback: Callback<Token?>?)

    @POST("/user/password")
    fun postUserPassword(@Header("x-access-token") token: String?, @Body query: Map<*, *>?, callback: Callback<Token?>?)

    @POST("/user/facebook")
    fun postUserFacebook(@Header("x-access-token") token: String?, @Body query: Map<*, *>?, callback: Callback<Token?>?)

    @DELETE("/user/facebook")
    fun deleteUserFacebook(@Header("x-access-token") token: String?, callback: Callback<Token?>?)

    @GET("/user/facebook")
    fun getUserFacebook(@Header("x-access-token") token: String?, callback: Callback<Facebook?>?)

    @POST("/user/device/{registration_id}")
    fun registerFirebaseToken(@Header("x-access-token") token: String?, @Path("registration_id") id: String?, callback: Callback<Response?>?)

    @DELETE("/user/device/{registration_id}")
    fun deleteFirebaseToken(@Header("x-access-token") token: String?, @Path("registration_id") id: String?, callback: Callback<Response?>?)

    @DELETE("/user/account")
    fun deleteUserAccount(@Header("x-access-token") token: String?, callback: Callback<Response?>?)

    // API for Notification
    @GET("/notification")
    fun getNotification(@Header("x-access-token") token: String?, @QueryMap query: Map<*, *>?, callback: Callback<List<Notification?>?>?)

    @GET("/notification/count")
    fun getNotificationCount(@Header("x-access-token") token: String?, callback: Callback<Map<String?, Int?>?>?)
}