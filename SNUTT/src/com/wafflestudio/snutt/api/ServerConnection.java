package com.wafflestudio.snutt.api;

import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class ServerConnection {
	public static final boolean DEBUG = false;
	
	public static final String BASE_URL = DEBUG ? "http://snutt.kr" : "http://snutt.kr";
	public static final String apiUrl = BASE_URL + "/api/";
	public static String deviceId;
	
	static AsyncHttpClient client = new AsyncHttpClient();

	public static void versionCheck(ServerCallback callback){
		String url = apiUrl + "/app_version.json";
		get(url, callback);
	}
	
	///////////////////////////////////////////////////////////////
	//private methods
	private static void get(String url, final ServerCallback callback){
		client.get(url, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject obj){
				callback.onSuccess(obj);
			}
			@Override
			public void handleFailureMessage(Throwable e, String responseBody){
				callback.onFailure(e, responseBody);
			}
		});
	}
	
//	private static ByteArrayEntity jsonToEntity(JSONObject json){
//		ByteArrayEntity entity = null;
//		try {
//			entity = new ByteArrayEntity(json.toString().getBytes("UTF-8"));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		return entity;
//	}
	
	public interface ServerCallback {
		public void onSuccess(JSONObject result);
		public void onFailure(Throwable e, String responseBody);
	}
	

}
