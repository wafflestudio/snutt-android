package com.wafflestudio.snutt;

import java.net.ResponseCache;
import java.util.Map;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by makesource on 2016. 1. 16..
 */
public interface SNUTTRestApi {

    @POST("/search_query")
    public void postSearchQuery(@Body Map query, Callback<Response> callback);
}
