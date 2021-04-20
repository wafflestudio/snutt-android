package com.wafflestudio.snutt2.ui

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseFragment
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.manager.UserManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by makesource on 2016. 3. 26..
 */
@AndroidEntryPoint
class SignUpFragment : SNUTTBaseFragment() {
    
    @Inject
    lateinit var userManager: UserManager

    @Inject
    lateinit var apiOnError: ApiOnError
    
    private var idEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var passwordConfirmEditText: EditText? = null
    private var emailEditText: EditText? = null
    private var termsTextView: TextView? = null
    private var signupButton: Button? = null
    private var facebookButton: LinearLayout? = null
    private var callbackManager: CallbackManager? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_signup, container, false)
        setTitle("회원가입")
        idEditText = rootView.findViewById<View>(R.id.input_id) as EditText
        passwordEditText = rootView.findViewById<View>(R.id.input_password) as EditText
        passwordConfirmEditText = rootView.findViewById<View>(R.id.input_password_confirm) as EditText
        emailEditText = rootView.findViewById<View>(R.id.input_email) as EditText
        signupButton = rootView.findViewById<View>(R.id.button_sign_up) as Button
        facebookButton = rootView.findViewById<View>(R.id.button_facebook) as LinearLayout
        termsTextView = rootView.findViewById<View>(R.id.terms_textview) as TextView
        signupButton!!.setOnClickListener(
            View.OnClickListener {
                val id = idEditText!!.text.toString()
                val password = passwordEditText!!.text.toString()
                val passwordConfirm = passwordConfirmEditText!!.text.toString()
                val email = emailEditText!!.text.toString()
                hideSoftKeyboard(requireView())
                if (password != passwordConfirm) {
                    Toast.makeText(app, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                val progressDialog = ProgressDialog.show(context, "회원가입", "잠시만 기다려 주세요", true, false)
                userManager.postSingUp(id, password, email)
                    .bindUi(
                        this,
                        onSuccess = {
                            baseActivity!!.startMain()
                            baseActivity!!.finishAll()
                            progressDialog.dismiss()
                        },
                        onError = {
                            progressDialog.dismiss()
                            apiOnError.invoke(it)
                        }
                    )
            }
        )
        facebookButton!!.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(
                this@SignUpFragment,
                null
            )
        }
        termsTextView!!.setOnClickListener {
            val url = getString(R.string.api_server) + getString(R.string.terms)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
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
                    userManager.postLoginFacebook(id, token)
                        .bindUi(this@SignUpFragment,
                            onSuccess = {
                                baseActivity!!.startMain()
                                baseActivity!!.finishAll()
                                progressDialog.dismiss()
                            },
                            onError = {
                                progressDialog.dismiss()
                                apiOnError.invoke(it)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }

    private fun setTitle(title: String) {
        requireActivity().title = title
    }

    companion object {
        private const val TAG = "SIGN_UP_FRAGMENT"

        @JvmStatic
        fun newInstance(): SignUpFragment {
            return SignUpFragment()
        }
    }
}
