package com.wafflestudio.snutt2.data.user

import com.wafflestudio.snutt2.lib.network.dto.*
import com.wafflestudio.snutt2.lib.network.dto.core.UserDto
import com.wafflestudio.snutt2.model.TableTrimParam
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val user: Flow<UserDto>

    val tableTrimParam: Flow<TableTrimParam>

    // login with local id
    suspend fun postSignIn(id: String, password: String)

    // login with facebook id
    suspend fun postLoginFacebook(facebookId: String, facebookToken: String)

    suspend fun postSingUp(id: String, password: String, email: String)

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
}