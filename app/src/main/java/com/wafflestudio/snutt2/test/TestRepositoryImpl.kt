package com.wafflestudio.snutt2.test

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.network.ErrorCode
import com.wafflestudio.snutt2.lib.network.NetworkError
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.call_adapter.ErrorParsedHttpException
import com.wafflestudio.snutt2.lib.network.dto.PostSignUpParams
import com.wafflestudio.snutt2.lib.network.toNetworkError
import javax.inject.Inject

class TestRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
) : TestRepository {
    override suspend fun registerLocal(id: String, password: String, email: String): Result<Unit> {
        try {
            api._postSignUp(PostSignUpParams(id, password, email))
            return Result.Success(Unit)
        } catch (e: ErrorParsedHttpException) {
            val displayMessage = e.errorDTO?.displayMessage ?: ""
            return when (e.errorDTO?.code) {
                ErrorCode.INVALID_ID -> Result.Fail(NetworkError.SignupError.InvalidId(displayMessage))
                ErrorCode.INVALID_PASSWORD -> Result.Fail(NetworkError.SignupError.InvalidPassword(displayMessage))
                ErrorCode.DUPLICATE_ID -> Result.Fail(NetworkError.SignupError.DuplicateId(displayMessage))
                ErrorCode.USED_EMAIL -> Result.Fail(NetworkError.SignupError.UsedEmail(displayMessage))
                else -> Result.Fail(e.toNetworkError())
            }
        } catch (e: Exception) {
            return Result.Fail(e.toNetworkError())
        }
    }

    override suspend fun getNotificationCount(): Result<Int> {
        try {
            val result = api._getNotificationCount()
            return Result.Success(result.count.toInt())
        } catch (e: ErrorParsedHttpException) {
            return Result.Fail(e.toNetworkError())
        } catch (e: Exception) {
            return Result.Fail(e.toNetworkError())
        }
    }

    override suspend fun clearToken() {
        storage.accessToken.clear()
    }
}
