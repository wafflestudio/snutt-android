package com.wafflestudio.snutt2.views.logged_out

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.FragmentSignUpBinding
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.Quadruple
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : BaseFragment() {

    private lateinit var binding: FragmentSignUpBinding

    @Inject
    lateinit var loginManager: LoginManager

    @Inject
    lateinit var callbackManager: CallbackManager

    @Inject
    lateinit var apiOnError: ApiOnError

    private val vm: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpButton.throttledClicks()
            .map {
                val id = binding.idInput.text.toString()
                val email = binding.emailInput.text.toString()
                val password = binding.passwordInput.text.toString()
                val passwordConfirm = binding.passwordConfirmInput.text.toString()
                Quadruple(id, email, password, passwordConfirm)
            }
            .filter { (id, email, password, passwordConfirm) ->
                val passCheck = password == passwordConfirm
                if (passCheck.not()) {
                    Toast.makeText(
                        context,
                        getString(R.string.sign_up_password_confirm_invalid_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                passCheck
            }
            .bindUi(this) { (id, email, password, _) ->
                hideSoftKeyboard()
                val progressDialog =
                    ProgressDialog.show(context, "회원가입", "잠시만 기다려 주세요", true, false)
                vm.signUpLocal(id, email, password)
                    .bindUi(this, onSuccess = {
                        routeHome()
                        progressDialog.dismiss()
                    }, onError = {
                        apiOnError(it)
                        progressDialog.dismiss()
                    })
            }

        binding.facebookSignUpButton.throttledClicks()
            .bindUi(this) {
                loginManager.logInWithReadPermissions(
                    this@SignUpFragment,
                    null
                )
            }

        binding.terms.throttledClicks()
            .bindUi(this) {
                showTerms()
            }

        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                if (result == null) {
                    Toast.makeText(
                        context,
                        getString(R.string.sign_up_facebook_login_failed_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                val id = result.accessToken.userId
                val token = result.accessToken.token
                vm.signUpFacebook(id, token)
            }

            override fun onCancel() {
                Toast.makeText(
                    context,
                    getString(R.string.sign_up_facebook_login_failed_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onError(error: FacebookException?) {
                Toast.makeText(
                    context,
                    getString(R.string.sign_up_facebook_login_failed_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun hideSoftKeyboard() {
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            requireView().windowToken,
            0
        )
    }

    private fun routeHome() {
        findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
    }

    private fun showTerms() {
        val url = getString(R.string.api_server) + getString(R.string.terms)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    @Suppress("DEPRECATION") // Facebook SDK 에서 ActivityResultContract 지원 안해줌
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

}
