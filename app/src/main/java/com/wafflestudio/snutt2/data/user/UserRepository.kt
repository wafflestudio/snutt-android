package com.wafflestudio.snutt2.data.user

import com.wafflestudio.snutt2.lib.network.dto.GetUserFacebookResults
import com.wafflestudio.snutt2.lib.network.dto.core.UserDto
import com.wafflestudio.snutt2.model.TableTrimParam
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val user: StateFlow<UserDto?>

    val tableTrimParam: StateFlow<TableTrimParam>

    val accessToken: StateFlow<String>

    // login with local id
    suspend fun postSignIn(id: String, password: String)

    // login with facebook id
    suspend fun postLoginFacebook(facebookId: String, facebookToken: String)

    suspend fun postSignUp(id: String, password: String, email: String)

    suspend fun fetchUserInfo()

    suspend fun putUserInfo(email: String)

    suspend fun deleteUserAccount()

    suspend fun putUserPassword(oldPassword: String, newPassword: String)

    suspend fun getUserFacebook(): GetUserFacebookResults

    // 새로운 local_id 추가
    suspend fun postUserPassword(id: String, password: String)

    suspend fun deleteUserFacebook()

    // facebook 계정 연동
    suspend fun postUserFacebook(
        facebookId: String,
        facebookToken: String
    )

    suspend fun postFeedback(email: String, detail: String)

    suspend fun deleteFirebaseToken()

    suspend fun postForceLogout()

    suspend fun setTableTrim(
        dayOfWeekFrom: Int? = null,
        dayOfWeekTo: Int? = null,
        hourFrom: Int? = null,
        hourTo: Int? = null,
        isAuto: Boolean? = null,
    )

    suspend fun getAccessToken(): String

    suspend fun performLogout()

    suspend fun registerToken()
}
