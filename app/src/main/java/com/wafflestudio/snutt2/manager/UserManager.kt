package com.wafflestudio.snutt2.manager

import android.util.Log
import com.facebook.login.LoginManager
import com.google.firebase.iid.FirebaseInstanceId
import com.wafflestudio.snutt2.SNUTTApplication
import com.wafflestudio.snutt2.model.Facebook
import com.wafflestudio.snutt2.model.Token
import com.wafflestudio.snutt2.model.User
import com.wafflestudio.snutt2.model.Version
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.util.*

/**
 * Created by makesource on 2016. 1. 16..
 */
class UserManager private constructor(private val app: SNUTTApplication) {
    ///////
    var user: User
        private set

    // login with local id
    fun postSignIn(id: String?, password: String?, callback: Callback<Any>) {
        val query: MutableMap<Any?, Any?> = HashMap<Any?, Any?>()
        query["id"] = id
        query["password"] = password
        app.restService!!.postSignIn(query, object : Callback<Token> {
            override fun success(token: Token, response: Response) {
                Log.d(TAG, "post local sign in success!!")
                Log.d(TAG, "token : " + token.token + " user_id : " + token.user_id)
                PrefManager.instance!!.prefKeyXAccessToken = token.token
                PrefManager.instance!!.prefKeyUserId = token.user_id
                instance!!.registerFirebaseToken(null)
                logUserTemp(token.user_id)
                callback?.success(token, response)
            }

            override fun failure(error: RetrofitError) {
                Log.w(TAG, "post local sign in failed!")
                callback?.failure(error)
            }
        })
    }

    // login with facebook id
    fun postLoginFacebook(facebookId: String?, facebookToken: String?, callback: Callback<Any>) {
        val query: MutableMap<Any?, Any?> = HashMap<Any?, Any?>()
        query["fb_id"] = facebookId
        query["fb_token"] = facebookToken
        app.restService!!.postLoginFacebook(query, object : Callback<Token> {
            override fun success(token: Token, response: Response) {
                Log.d(TAG, "post user facebook success!")
                Log.d(TAG, "token : " + token.token + " user_id : " + token.user_id)
                PrefManager.instance!!.prefKeyXAccessToken = token.token
                PrefManager.instance!!.prefKeyUserId = token.user_id
                instance!!.registerFirebaseToken(null)
                logUserTemp(token.user_id)
                callback?.success(token, response)
            }

            override fun failure(error: RetrofitError) {
                Log.w(TAG, "post user facebook failed!")
                callback?.failure(error)
            }
        })
    }

    fun postSingUp(id: String?, password: String?, email: String?, callback: Callback<Token>) {
        // id, password -> regex check!
        val query: MutableMap<Any?, Any?> = HashMap<Any?, Any?>()
        query["id"] = id
        query["password"] = password
        query["email"] = email
        app.restService!!.postSignUp(query, object : Callback<Token> {
            override fun success(token: Token, response: Response) {
                Log.d(TAG, "post sign up success!")
                Log.d(TAG, "token : " + token.token + " user_id : " + token.user_id)
                PrefManager.instance!!.prefKeyXAccessToken = token.token
                PrefManager.instance!!.prefKeyUserId = token.user_id
                instance!!.registerFirebaseToken(null)
                logUserTemp(token.user_id)
                callback?.success(token, response)
            }

            override fun failure(error: RetrofitError) {
                callback?.failure(error)
            }
        })
    }

    fun getUserInfo(callback: Callback<User>) {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        app.restService!!.getUserInfo(token, object : Callback<User> {
            override fun success(user: User, response: Response) {
                Log.d(TAG, "get user info success")
                this@UserManager.user = user
                callback?.success(user, response)
            }

            override fun failure(error: RetrofitError) {
                Log.d(TAG, "get user info failed")
                callback?.failure(error)
            }
        })
    }

    fun putUserInfo(email: String?, callback: Callback<Any>) {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        val query: MutableMap<Any?, Any?> = HashMap<Any?, Any?>()
        query["email"] = email
        app.restService!!.putUserInfo(token, query, object : Callback<Response> {
            override fun success(response: Response?, response2: Response) {
                Log.d(TAG, "put user info success")
                user.email = email
                callback?.success(response, response2)
            }

            override fun failure(error: RetrofitError) {
                Log.d(TAG, "put user info failed")
                callback?.failure(error)
            }
        })
    }

    fun deleteUserAccount(callback: Callback<Any>) {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        app.restService!!.deleteUserAccount(token, object : Callback<Response> {
            override fun success(response: Response?, response2: Response) {
                Log.d(TAG, "delete user account success")
                callback?.success(response, response2)
            }

            override fun failure(error: RetrofitError) {
                Log.d(TAG, "get delete user account failed")
                callback?.failure(error)
            }
        })
    }

    fun putUserPassword(oldPassword: String?, newPassword: String?, callback: Callback<Any>) {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        val query: MutableMap<Any?, Any?> = HashMap<Any?, Any?>()
        query["old_password"] = oldPassword
        query["new_password"] = newPassword
        app.restService!!.putUserPassword(token, query, object : Callback<Token> {
            override fun success(token: Token, response: Response) {
                Log.d(TAG, "put user password success")
                PrefManager.instance!!.prefKeyXAccessToken == token.token
                callback?.success(token, response)
            }

            override fun failure(error: RetrofitError) {
                Log.d(TAG, "put user password failed")
                callback?.failure(error)
            }
        })
    }

    fun getUserFacebook(callback: Callback<Facebook>) {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        app.restService!!.getUserFacebook(token, object : Callback<Facebook> {
            override fun success(facebook: Facebook?, response: Response) {
                Log.d(TAG, "get user facebook success!")
                callback?.success(facebook, response)
            }

            override fun failure(error: RetrofitError) {
                Log.w(TAG, "get user facebook failed")
                callback?.failure(error)
            }
        })
    }

    // 새로운 local_id 추가
    fun postUserPassword(id: String?, password: String?, callback: Callback<Any>) {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        val query: MutableMap<Any?, Any?> = HashMap<Any?, Any?>()
        query["id"] = id
        query["password"] = password
        app.restService!!.postUserPassword(token, query, object : Callback<Token> {
            override fun success(token: Token, response: Response) {
                Log.d(TAG, "post user password success!")
                PrefManager.instance!!.prefKeyXAccessToken = token.token
                callback?.success(token, response)
            }

            override fun failure(error: RetrofitError) {
                Log.w(TAG, "post user password failed")
                callback?.failure(error)
            }
        })
    }

    fun deleteUserFacebook(callback: Callback<Any>) {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        app.restService!!.deleteUserFacebook(token, object : Callback<Token> {
            override fun success(token: Token, response: Response) {
                Log.d(TAG, "delete user facebook success!")
                PrefManager.instance!!.prefKeyXAccessToken = token.token
                callback?.success(token, response)
            }

            override fun failure(error: RetrofitError) {
                Log.w(TAG, "delete user facebook failed")
                callback?.failure(error)
            }
        })
    }

    // facebook 계정 연동
    fun postUserFacebook(facebookId: String?, facebookToken: String?, callback: Callback<Any>) {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        val query: MutableMap<Any?, Any?> = HashMap<Any?, Any?>()
        query["fb_id"] = facebookId
        query["fb_token"] = facebookToken
        app.restService!!.postUserFacebook(token, query, object : Callback<Token> {
            override fun success(token: Token, response: Response) {
                Log.d(TAG, "post user facebook success!")
                PrefManager.instance!!.prefKeyXAccessToken = token.token
                callback?.success(token, response)
            }

            override fun failure(error: RetrofitError) {
                Log.w(TAG, "post user facebook failed")
                callback?.failure(error)
            }
        })
    }

    fun getAppVersion(callback: Callback<Any>) {
        app.restService!!.getAppVersion(object : Callback<Version> {
            override fun success(version: Version?, response: Response) {
                Log.d(TAG, "get app version success!")
                callback?.success(version, response)
            }

            override fun failure(error: RetrofitError) {
                Log.w(TAG, "get app version failed")
                callback?.failure(error)
            }
        })
    }

    fun postFeedback(email: String?, detail: String?, callback: Callback<Any>) {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        val query: MutableMap<Any?, Any?> = HashMap<Any?, Any?>()
        query["email"] = email
        query["message"] = detail
        app.restService!!.postFeedback(token, query, object : Callback<Response> {
            override fun success(response: Response?, response2: Response) {
                Log.d(TAG, "post feedback success!")
                callback?.success(response, response2)
            }

            override fun failure(error: RetrofitError) {
                Log.w(TAG, "post feedback failed")
                callback?.failure(error)
            }
        })
    }

    fun registerFirebaseToken(callback: Callback<Any>?) {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        val firebaseToken = FirebaseInstanceId.getInstance().token
        app.restService!!.registerFirebaseToken(token, firebaseToken, object : Callback<Response> {
            override fun success(response: Response?, response2: Response) {
                Log.d(TAG, "register firebase token success!")
                Log.d(TAG, "token : $firebaseToken")
                callback?.success(response, response2)
            }

            override fun failure(error: RetrofitError) {
                Log.w(TAG, "register firebase token failed.")
                callback?.failure(error)
            }
        })
    }

    fun deleteFirebaseToken(callback: Callback<Any>) {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        val firebaseToken = FirebaseInstanceId.getInstance().token
        app.restService!!.deleteFirebaseToken(token, firebaseToken, object : Callback<Response> {
            override fun success(response: Response?, response2: Response) {
                Log.d(TAG, "delete firebase token success!")
                Log.d(TAG, "token : $firebaseToken")
                callback?.success(response, response2)
            }

            override fun failure(error: RetrofitError) {
                Log.w(TAG, "delete firebase token failed.")
                callback?.failure(error)
            }
        })
    }

    fun postForceLogout(callback: Callback<Any>) {
        val user_id: String? = PrefManager.instance!!.prefKeyUserId
        val firebaseToken = FirebaseInstanceId.getInstance().token
        val query: MutableMap<Any?, Any?> = HashMap<Any?, Any?>()
        query["user_id"] = user_id
        query["registration_id"] = firebaseToken
        app.restService!!.postForceLogout(query, object : Callback<Response> {
            override fun success(response: Response?, response2: Response) {
                Log.d(TAG, "post force logout success")
                callback?.success(response, response2)
            }

            override fun failure(error: RetrofitError) {
                Log.w(TAG, "post force logout failed..")
                callback?.failure(error)
            }
        })
    }

    fun performLogout() {
        /* firebase token 삭제 후 로그아웃 시행 */
        LectureManager.instance!!.reset()
        NotiManager.instance!!.reset()
        TableManager.instance!!.reset()
        TagManager.instance!!.reset()
        PrefManager.instance!!.resetPrefValue()
        LoginManager.getInstance().logOut() // for facebook sdk
        user = User()
    }

    // Refactor FIXME
    private fun logUserTemp(id: String?) {
        // do nothing
    }

    companion object {
        private const val TAG = "USER_MANAGER"
        private var singleton: UserManager? = null
        fun getInstance(app: SNUTTApplication): UserManager? {
            if (singleton == null) {
                singleton = UserManager(app)
            }
            return singleton
        }

        @JvmStatic
        val instance: UserManager?
            get() {
                if (singleton == null) Log.e(TAG, "This method should not be called at this time!!")
                return singleton
            }
    }

    /**
     * UserManager 싱글톤
     */
    init {
        user = User()
    }
}