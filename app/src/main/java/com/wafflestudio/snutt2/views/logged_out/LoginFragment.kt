package com.wafflestudio.snutt2.views.logged_out

import android.content.Context
import android.content.Intent
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
import com.wafflestudio.snutt2.databinding.FragmentLoginBinding
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.network.ApiStatus
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    private lateinit var binding: FragmentLoginBinding

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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.loginStatus
            .bindUi(this) {
                when (it) {
                    is ApiStatus.Success -> {
                        routeHome()
                    }
                    is ApiStatus.Failure -> {
                        apiOnError(it.error)
                    }
                    is ApiStatus.Loading -> {
//                        TODO: loading spinner?
                    }
                    else -> {

                    }
                }
            }

        binding.loginButton.throttledClicks()
            .map {
                val id = binding.idInput.text.toString()
                val password = binding.passwordInput.text.toString()
                Pair(id, password)
            }
            .bindUi(this) { (id, password) ->
                hideSoftKeyboard()
                vm.loginLocal(id, password)
            }

        binding.facebookLoginButton.throttledClicks()
            .bindUi(this) {
                loginManager.logInWithReadPermissions(
                    this@LoginFragment,
                    null
                )
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
                vm.loginFacebook(id, token)
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
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

    @Suppress("DEPRECATION") // Facebook SDK 에서 ActivityResultContract 지원 안해줌
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
