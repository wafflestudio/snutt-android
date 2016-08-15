package com.wafflestudio.snutt;

import com.wafflestudio.snutt.model.Lecture;
import com.wafflestudio.snutt.model.Table;
import com.wafflestudio.snutt.model.TagList;
import com.wafflestudio.snutt.model.Token;

import java.net.ResponseCache;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by makesource on 2016. 1. 16..
 */
public interface SNUTTRestApi {

    // API Basics and Auth

    @POST("/auth/register_local")
    public void postSignUp(@Body Map query, Callback<Response> callback);

    @POST("/auth/login_local")
    public void postSignIn(@Body Map query, Callback<Token> callback);

    @POST("/search_query")
    public void postSearchQuery(@Body Map query, Callback<List<Lecture>> callback);


    // API Timetable

    @GET("/tables")
    public void getTableList(@Header("x-access-token") String token, Callback<List<Table>> callback);

    @GET("/tables/{id}")
    public void getTableById(@Header("x-access-token") String token, @Path("id") String id, Callback<Table> callback);

    @GET("/tables/recent")
    public void getRecentTable(@Header("x-access-token") String token, Callback<Table> callback);

    @GET("/tags/{year}/{semester}")
    public void getTagList(@Path("year") int year, @Path("semester") int semester, Callback<TagList> callback);
}
