package com.wafflestudio.snutt2.views.logged_out

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.lib.network.ApiStatus
import com.wafflestudio.snutt2.lib.network.bindStatus
import com.wafflestudio.snutt2.manager.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val userRepository: UserRepository
) : ViewModel() {

    private val apiStatusSubject: BehaviorSubject<ApiStatus<Unit>> =
        BehaviorSubject.createDefault(ApiStatus.Default())

    val loginStatus: Observable<ApiStatus<Unit>> = apiStatusSubject.hide()

    fun loginLocal(id: String, email: String) {
        userRepository.postSignIn(id, email)
            .map { }
            .bindStatus(apiStatusSubject)
            .subscribeBy(onError = {})
    }

    fun signUpLocal(id: String, email: String, password: String) {
        userRepository.postSingUp(id, password, email)
            .map { }
            .bindStatus(apiStatusSubject)
            .subscribeBy(onError = {})
    }

    fun loginFacebook(id: String, token: String) {
        userRepository.postLoginFacebook(id, token)
            .map { }
            .bindStatus(apiStatusSubject)
            .subscribeBy(onError = {})
    }

    fun signUpFacebook(id: String, token: String) {
        userRepository.postLoginFacebook(id, token)
            .map { }
            .bindStatus(apiStatusSubject)
            .subscribeBy(onError = {})
    }
}
