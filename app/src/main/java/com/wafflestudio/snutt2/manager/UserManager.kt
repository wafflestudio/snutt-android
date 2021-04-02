package com.wafflestudio.snutt2.manager;

import android.util.Log;

import com.facebook.login.LoginManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.wafflestudio.snutt2.SNUTTApplication;
import com.wafflestudio.snutt2.model.Facebook;
import com.wafflestudio.snutt2.model.Token;
import com.wafflestudio.snutt2.model.User;
import com.wafflestudio.snutt2.model.Version;

import java.util.HashMap;
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

    ///////

    public User getUser() {
        return me;
    }

    // login with local id
    public void postSignIn(String id, String password, final Callback callback) {
        Map query = new HashMap();
        query.put("id", id);
        query.put("password", password);
        app.getRestService().postSignIn(query, new Callback<Token>() {
            @Override
            public void success(Token token, Response response) {
                Log.d(TAG, "post local sign in success!!");
                Log.d(TAG, "token : " + token.getToken() + " user_id : " + token.getUser_id());
                PrefManager.getInstance().setPrefKeyXAccessToken(token.getToken());
                PrefManager.getInstance().setPrefKeyUserId(token.getUser_id());
                UserManager.getInstance().registerFirebaseToken(null);
                logUserTemp(token.getUser_id());
                if (callback != null) callback.success(token, response);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "post local sign in failed!");
                if (callback != null) callback.failure(error);
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
                    Log.d(TAG, "token : " + token.getToken() + " user_id : " + token.getUser_id());
                    PrefManager.getInstance().setPrefKeyXAccessToken(token.getToken());
                    PrefManager.getInstance().setPrefKeyUserId(token.getUser_id());
                    UserManager.getInstance().registerFirebaseToken(null);
                    logUserTemp(token.getUser_id());
                    if (callback != null) callback.success(token, response);
                }

                @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "post user facebook failed!");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void postSingUp(String id, String password, String email, final Callback callback) {
        // id, password -> regex check!
        Map query = new HashMap();
        query.put("id", id);
        query.put("password", password);
        query.put("email", email);
        app.getRestService().postSignUp(query, new Callback<Token>() {
            @Override
            public void success(Token token, Response response) {
                Log.d(TAG, "post sign up success!");
                Log.d(TAG, "token : " + token.getToken() + " user_id : " + token.getUser_id());
                PrefManager.getInstance().setPrefKeyXAccessToken(token.getToken());
                PrefManager.getInstance().setPrefKeyUserId(token.getUser_id());
                UserManager.getInstance().registerFirebaseToken(null);
                logUserTemp(token.getUser_id());
                if (callback != null) callback.success(token, response);
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
        final String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        final String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        app.getRestService().registerFirebaseToken(token, firebaseToken, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.d(TAG, "register firebase token success!");
                Log.d(TAG, "token : " + firebaseToken);
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
        final String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        final String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        app.getRestService().deleteFirebaseToken(token, firebaseToken, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.d(TAG, "delete firebase token success!");
                Log.d(TAG, "token : " + firebaseToken);
                if (callback != null) callback.success(response, response2);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "delete firebase token failed.");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void postForceLogout(final Callback callback) {
        final String user_id = PrefManager.getInstance().getPrefKeyUserId();
        final String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        final Map query = new HashMap();
        query.put("user_id", user_id);
        query.put("registration_id", firebaseToken);
        app.getRestService().postForceLogout(query, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.d(TAG, "post force logout success");
                if (callback != null) callback.success(response, response2);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.w(TAG, "post force logout failed..");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void performLogout() {
        /* firebase token 삭제 후 로그아웃 시행 */
        LectureManager.getInstance().reset();
        NotiManager.getInstance().reset();
        TableManager.getInstance().reset();
        TagManager.getInstance().reset();
        PrefManager.getInstance().resetPrefValue();
        LoginManager.getInstance().logOut(); // for facebook sdk
        me = new User();
    }

    // Refactor FIXME
    private void logUserTemp(String id) {
        // do nothing
    }
}
