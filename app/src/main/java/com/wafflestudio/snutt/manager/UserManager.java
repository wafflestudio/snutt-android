package com.wafflestudio.snutt.manager;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.wafflestudio.snutt.SNUTTApplication;
import com.wafflestudio.snutt.SNUTTBaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class UserManager {
    private static final String TAG = "LECTURE_MANAGER" ;

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
        app.getRestService().postSignIn(query, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.d(TAG, "post sign in success!!");
                Log.d(TAG, "token : " + s);
                notifySingIn(true);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "post sign in failed");
                notifySingIn(false);
            }
        });

    }

    private void notifySingIn(boolean code) {
        for (OnUserDataChangedListener listener : listeners) {
            listener.notifySignIn(code);
        }
    }

}
