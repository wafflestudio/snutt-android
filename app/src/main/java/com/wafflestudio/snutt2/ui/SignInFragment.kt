package com.wafflestudio.snutt2.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseFragment
import com.wafflestudio.snutt2.manager.UserManager.Companion.instance

/**
 * Created by makesource on 2016. 3. 26..
 */
class SignInFragment : SNUTTBaseFragment() {
    private var idEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var signinButton: Button? = null
    private var facebookButton: LinearLayout? = null
    private var callbackManager: CallbackManager? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_signin, container, false)
        setTitle("로그인")
        idEditText = rootView.findViewById<View>(R.id.input_id) as EditText
        passwordEditText = rootView.findViewById<View>(R.id.input_password) as EditText
        signinButton = rootView.findViewById<View>(R.id.button_sign_in) as Button
        facebookButton = rootView.findViewById<View>(R.id.button_facebook) as LinearLayout
        signinButton!!.setOnClickListener {
            val id = idEditText!!.text.toString()
            val password = passwordEditText!!.text.toString()
            hideSoftKeyboard(view!!)
            val progressDialog = ProgressDialog.show(context, "로그인", "잠시만 기다려 주세요", true, false)

            instance!!.postSignIn(id, password)
                .bindUi(
                    this,
                    onSuccess = {
                        baseActivity!!.startMain()
                        baseActivity!!.finishAll()
                        progressDialog.dismiss()
                    },
                    onError = {
                        progressDialog.dismiss()
                    }
                )
        }
        facebookButton!!.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(
                this@SignInFragment,
                null
            )
        }
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    // App code
                    val id = loginResult.accessToken.userId
                    val token = loginResult.accessToken.token
                    val progressDialog = ProgressDialog.show(
                        context,
                        "로그인",
                        "잠시만 기다려 주세요",
                        true,
                        false
                    )
                    instance!!.postLoginFacebook(id, token)
                        .bindUi(
                            this@SignInFragment,
                            onSuccess = {
                                baseActivity!!.startMain()
                                baseActivity!!.finishAll()
                                progressDialog.dismiss()
                            },
                            onError = {
                                progressDialog.dismiss()
                            }
                        )
                }

                override fun onCancel() {
                    // App code
                    Log.w(TAG, "Cancel")
                    Toast.makeText(app, "페이스북 연동중 오류가 발생하였습니다", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException) {
                    // App code
                    Log.e(TAG, "Error", error)
                    Toast.makeText(app, "페이스북 연동중 오류가 발생하였습니다", Toast.LENGTH_SHORT).show()
                }
            }
        )
        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }

    private fun setTitle(title: String) {
        activity!!.title = title
    }

    companion object {
        @JvmStatic
        fun newInstance(): SignInFragment {
            return SignInFragment()
        }

        private const val TAG = "SIGN_IN_FRAGMENT"
    }
}
