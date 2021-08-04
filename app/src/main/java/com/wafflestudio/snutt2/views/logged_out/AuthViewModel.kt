package com.wafflestudio.snutt2.views.logged_out

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.UserRepository
import com.wafflestudio.snutt2.handler.ApiOnError
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val userRepository: UserRepository,
    val apiOnError: ApiOnError
) : ViewModel() {

    fun loginLocal(id: String, email: String): Single<Unit> {
        return userRepository.postSignIn(id, email)
            .map { }
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun signUpLocal(id: String, email: String, password: String): Single<Unit> {
        return userRepository.postSingUp(id, password, email)
            .map { }
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun loginFacebook(id: String, token: String): Single<Unit> {
        return userRepository.postLoginFacebook(id, token)
            .map { }
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun signUpFacebook(id: String, token: String): Single<Unit> {
        return userRepository.postLoginFacebook(id, token)
            .map { }
            .observeOn(AndroidSchedulers.mainThread())
    }
}
