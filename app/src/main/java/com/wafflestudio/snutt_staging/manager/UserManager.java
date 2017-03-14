package com.wafflestudio.snutt_staging.manager;

import android.os.AsyncTask;
import android.util.Log;

import com.facebook.login.LoginManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.wafflestudio.snutt_staging.SNUTTApplication;
import com.wafflestudio.snutt_staging.model.Facebook;
import com.wafflestudio.snutt_staging.model.Token;
import com.wafflestudio.snutt_staging.model.User;
import com.wafflestudio.snutt_staging.model.Version;

import java.io.IOException;
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
    private static final String TAG = "USER_MANAGER" ;

    private SNUTTApplication app;

    private static UserManager singleton;
    private User me;

    /**
     * UserManager 싱글톤
     */

    private UserManager(SNUTTApplication app) {
        this.app = app;
        this.me = new User();
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

    public User getUser() {
        return me;
    }

    // login with local id
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

    // login with facebook id
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
                me = user;
                if (callback != null) callback.success(user, response);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "get user info failed");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void putUserInfo(final String email, final Callback callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        Map query = new HashMap();
        query.put("email", email);
        app.getRestService().putUserInfo(token, query, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.d(TAG, "put user info success");
                me.setEmail(email);
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

    public void getAppVersion(final Callback callback) {
        app.getRestService().getAppVersion(new Callback<Version>() {
            @Override
            public void success(Version version, Response response) {
                Log.d(TAG, "get app version success!");
                if (callback != null) callback.success(version, response);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "get app version failed");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void postFeedback(String email, String detail, final Callback callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        Map query = new HashMap();
        query.put("email", email);
        query.put("message", detail);
        app.getRestService().postFeedback(token, query, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.d(TAG, "post feedback success!");
                if (callback != null) callback.success(response, response2);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "post feedback failed");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void registerFirebaseToken(final Callback callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        Map query = new HashMap();
        query.put("registration_id", firebaseToken);
        app.getRestService().registerFirebaseToken(token, query, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.d(TAG, "register firebase token success!");
                if (callback != null) callback.success(response, response2);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "register firebase token failed.");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void deleteFirebaseToken(final Callback callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        Map query = new HashMap();
        query.put("registration_id", firebaseToken);
        app.getRestService().deleteFirebaseToken(token, query, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.d(TAG, "delete firebase token success!");
                if (callback != null) callback.success(response, response2);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "delete firebase token failed.");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void performLogout() {
        //UserManager.getInstance().deleteFirebaseToken(null);
        PrefManager.getInstance().resetPrefValue();
        LoginManager.getInstance().logOut(); // for facebook sdk
        deleteFirebaseInstanceId();
        me = null;
    }

    public void deleteFirebaseInstanceId() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                Log.d(TAG, msg);
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                    msg = "Device unRegistered";
                    Log.d(TAG, msg);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }
        }.execute(null, null, null);
    }

    private void notifySingIn(boolean code) {
        for (OnUserDataChangedListener listener : listeners) {
            listener.notifySignIn(code);
        }
    }

}
