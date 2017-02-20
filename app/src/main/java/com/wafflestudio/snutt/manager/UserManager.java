package com.wafflestudio.snutt.manager;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.wafflestudio.snutt.SNUTTApplication;
import com.wafflestudio.snutt.SNUTTBaseActivity;
import com.wafflestudio.snutt.model.Facebook;
import com.wafflestudio.snutt.model.Token;
import com.wafflestudio.snutt.model.User;

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

    //

    public void postLoginFacebook(String facebookId, String facebookToken, final Callback callback) {
        Map query = new HashMap();
        query.put("fb_id", facebookId);
        query.put("fb_token", facebookToken);
            app.getRestService().postLoginFacebook(query, new Callback<Token>() {
                @Override
                public void success(Token token, Response response) {
                    Log.d(TAG, "post user facebook success!");
                    PrefManager.getInstance().setPrefKeyXAccessToken(token.getToken());
                    notifySingIn(true);
                    if (callback != null) callback.success(token, response);
                }

                @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "post user facebook failed!");
                if (callback != null) callback.failure(error);
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

    public void getUserInfo(final Callback callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        app.getRestService().getUserInfo(token, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                Log.d(TAG, "get user info success");
                if (callback != null) callback.success(user, response);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "get user info failed");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void putUserInfo(String email, final Callback callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        Map query = new HashMap();
        query.put("email", email);
        app.getRestService().putUserInfo(token, query, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.d(TAG, "put user info success");
                if (callback != null) callback.success(response, response2);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "put user info failed");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void deleteUserAccount(final Callback callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        app.getRestService().deleteUserAccount(token, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.d(TAG, "delete user account success");
                if (callback != null) callback.success(response, response2);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "get delete user account failed");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void putUserPassword(String oldPassword, String newPassword, final Callback callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        Map query = new HashMap();
        query.put("old_password", oldPassword);
        query.put("new_password", newPassword);
        app.getRestService().putUserPassword(token, query, new Callback<Token>() {
            @Override
            public void success(Token token, Response response) {
                Log.d(TAG, "put user password success");
                PrefManager.getInstance().setPrefKeyXAccessToken(token.getToken());
                if (callback != null) callback.success(token, response);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "put user password failed");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void getUserFacebook(final Callback callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        app.getRestService().getUserFacebook(token, new Callback<Facebook>() {
            @Override
            public void success(Facebook facebook, Response response) {
                Log.d(TAG, "get user facebook success!");
                if (callback != null) callback.success(facebook, response);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "get user facebook failed");
                if (callback != null) callback.failure(error);
            }
        });
    }

    // 새로운 local_id 추가
    public void postUserPassword(String id, String password, final Callback callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        Map query = new HashMap();
        query.put("id", id);
        query.put("password", password);
        app.getRestService().postUserPassword(token, query, new Callback<Token>() {
            @Override
            public void success(Token token, Response response) {
                Log.d(TAG, "post user password success!");
                PrefManager.getInstance().setPrefKeyXAccessToken(token.getToken());
                if (callback != null) callback.success(token, response);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "post user password failed");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void deleteUserFacebook(final Callback callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        app.getRestService().deleteUserFacebook(token, new Callback<Token>() {
            @Override
            public void success(Token token, Response response) {
                Log.d(TAG, "delete user facebook success!");
                PrefManager.getInstance().setPrefKeyXAccessToken(token.getToken());
                if (callback != null) callback.success(token, response);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "delete user facebook failed");
                if (callback != null) callback.failure(error);
            }
        });
    }

    // facebook 계정 연동
    public void postUserFacebook(String facebookId, String facebookToken, final Callback callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        Map query = new HashMap();
        query.put("fb_id", facebookId);
        query.put("fb_token", facebookToken);
        app.getRestService().postUserFacebook(token, query, new Callback<Token>() {
            @Override
            public void success(Token token, Response response) {
                Log.d(TAG, "post user facebook success!");
                PrefManager.getInstance().setPrefKeyXAccessToken(token.getToken());
                if (callback != null) callback.success(token, response);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "post user facebook failed");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void performLogout() {
        PrefManager.getInstance().resetPrefValue();
        LoginManager.getInstance().logOut();
    }

    private void notifySingIn(boolean code) {
        for (OnUserDataChangedListener listener : listeners) {
            listener.notifySignIn(code);
        }
    }

}
