package com.wafflestudio.snutt;

import com.wafflestudio.snutt.model.Lecture;
import com.wafflestudio.snutt.model.Table;

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

/**
 * Created by makesource on 2016. 1. 16..
 */
public interface SNUTTRestApi {

    @POST("/search_query")
    public void postSearchQuery(@Body Map query, Callback<List<Lecture>> callback);

    @GET("/tables")
    public void getTableList(@Header("x-access-token") String token, Callback<List<Table>> callback);
}
