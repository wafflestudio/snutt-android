package com.wafflestudio.snutt.manager;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wafflestudio.snutt.SNUTTApplication;
import com.wafflestudio.snutt.SNUTTBaseActivity;
import com.wafflestudio.snutt.model.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class UserManager {
    private static final String TAG = "USER_MANAGER" ;

    private SNUTTApplication app;

    private static UserManager singleton;

    /**
     * UserManager 싱글톤
     */

    private UserManager(SNUTTApplication app) {
        this.app = app;
    }

    public static UserManager getInstance(SNUTTApplication app) {
        if (singleton == null) {
            singleton = new UserManager(app);
        }
        return singleton;
    }

    public static UserManager getInstance() {
        if (singleton == null) Log.e(TAG, "This method should not be called at this time!!");
        return singleton;
    }

    public interface OnUserDataChangedListener {
        void notifySignIn(boolean code);
    }

    private List<OnUserDataChangedListener> listeners = new ArrayList<>();

    public void addListener(OnUserDataChangedListener listener) {
        for (int i = 0; i < listeners.size(); i++) {
            if (listeners.get(i).equals(listener)) {
                Log.w(TAG, "listener reference is duplicated !!");
                return;
            }
        }
        listeners.add(listener);
    }

    public void removeListener(OnUserDataChangedListener listener) {
        for (int i=0;i<listeners.size(); i++) {
            OnUserDataChangedListener reference = listeners.get(i);
            if (reference == listener) {
                listeners.remove(i);
                break;
            }
        }
    }

    ///////


    public void postSignIn(String id, String password) {
        Map query = new HashMap();
        query.put("id", id);
        query.put("password", password);
        app.getRestService().postSignIn(query, new Callback<Token>() {
            @Override
            public void success(Token token, Response response) {
                Log.d(TAG, "post sign in success!!");
                Log.d(TAG, "token : " + token.getToken());
                PrefManager.getInstance().setPrefKeyXAccessToken(token.getToken());
                notifySingIn(true);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "post sign in failed");
                System.out.println(error);
                // TODO : (Seongwon) for test!!
                //notifySingIn(false);
                //notifySingIn(true);
            }
        });
    }

    public void postSingUp(String id, String password, final Callback callback) {
        // id, password -> regex check!
        Map query = new HashMap();
        query.put("id", id);
        query.put("password", password);
        app.getRestService().postSignUp(query, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                if (callback != null) callback.success(response, response2);
            }
            @Override
            public void failure(RetrofitError error) {
                if (callback != null) callback.failure(error);
            }
        });
    }

    private void notifySingIn(boolean code) {
        for (OnUserDataChangedListener listener : listeners) {
            listener.notifySignIn(code);
        }
    }

}
