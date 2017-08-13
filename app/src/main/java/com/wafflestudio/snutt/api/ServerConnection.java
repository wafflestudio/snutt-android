package com.wafflestudio.snutt.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.activity.main.MainActivity;
import com.wafflestudio.snutt.util.App;
import com.wafflestudio.snutt.util.Downloader;
import com.wafflestudio.snutt.util.SharedPrefUtil;

public class ServerConnection {
	public static final boolean DEBUG = false;
	
	public static final String BASE_URL = DEBUG ? "http://snutt.kr" : "http://snutt.kr";
	public static final String apiUrl = BASE_URL + "/api/";
	public static String deviceId;
	
	static AsyncHttpClient client = new AsyncHttpClient();

	//최신 버전 체크
	public static void versionCheck(ServerCallback callback){
		String url = apiUrl + "/app_version.json";
		get(url, callback);
	}
	
	//수강편람 업데이트 체크
	public static void sugangCheck(final MainActivity activity){
		final String url = apiUrl + "/sugang.json";
		get(url, new ServerCallback() {
			public void onSuccess(JSONObject result) {
				final long currentTimestamp = SharedPrefUtil.getInstance().getLong(SharedPrefUtil.PREF_KEY_SUGANG_TIMESTAMP);
				final long timestamp = result.optLong("updated_at", 0);
				
				if (currentTimestamp < timestamp){
					//Confirm
					activity.runOnUiThread(new Runnable() {
						public void run() {
							new AlertDialog.Builder(activity)
							.setIcon(android.R.drawable.ic_dialog_info)
							.setTitle(R.string.update_title)
							.setMessage(R.string.update_body)
							.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// 수강편람 다운로드 받고 업데이트
									String downloadUrl = ServerConnection.BASE_URL + "/data/snutt/data.zip";
									Downloader.getInstance().download(activity, downloadUrl, activity.getString(R.string.sugang), "/sugang.zip", new Runnable() {
										@Override
										public void run() {
											try {
												unpackageSugang();
												SharedPrefUtil.getInstance().setLong(SharedPrefUtil.PREF_KEY_SUGANG_TIMESTAMP, timestamp);
												activity.openSugangSelector();
											} catch (Exception e){
												e.printStackTrace();
											}
										}
									});
								}

							}).setNegativeButton(R.string.no, null).show();				
						}
					});
					
				}
			}
			public void onFailure(Throwable e, String responseBody) {
				e.printStackTrace();
			}
		});
	}
	
	///////////////////////////////////////////////////////////////
	//private methods
	public static void unpackageSugang() throws Exception {
		Context context = App.getAppContext();
		// sugang 폴더에 압축이 풀림 
		File file = new File(context.getFilesDir() + "/sugang.zip");
		File tmpFolder = new File(context.getFilesDir() + "/sugang_tmp");
		tmpFolder.mkdirs();
		FileInputStream zipFileStream = new FileInputStream(file);

		ZipInputStream zin = new ZipInputStream(zipFileStream);

		try {
			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null) {
				File outFile = new File(context.getFilesDir() + "/sugang_tmp/" + ze.getName());

				if (ze.isDirectory()) {
					outFile.mkdirs();
				}
				else {
					FileOutputStream fout = new FileOutputStream(outFile);
					byte bytes[] = new byte[1024];
					int read;
					try {
						while ((read = zin.read(bytes, 0, 1024)) >= 0){
							fout.write(bytes, 0, read);
						}
						zin.closeEntry();
					}
					finally {
						fout.close();
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		finally {
			zin.close();
		}
		//sugang_tmp 폴더를 sugang 으로 rename
		File targetFolder = new File(context.getFilesDir() + "/sugang");
		DeleteRecursive(targetFolder);
		tmpFolder.renameTo(targetFolder);
		file.delete();
	}
	
	private static void DeleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	            DeleteRecursive(child);

	    fileOrDirectory.delete();
	}
	
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
