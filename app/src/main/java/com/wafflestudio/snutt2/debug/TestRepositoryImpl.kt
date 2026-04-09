package com.wafflestudio.snutt2.debug

import com.wafflestudio.snutt2.storage.SNUTTStorage
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.network.api.SNUTTRestApi
import com.wafflestudio.snutt2.network.dto.PostSignUpParams
import com.wafflestudio.snutt2.network.error.toDomainError
import javax.inject.Inject

class TestRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
) : TestRepository {
    override suspend fun registerLocal(id: String, password: String, email: String): Result<Unit> {
        try {
            api._postSignUp(PostSignUpParams(id, password, email))
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getNotificationCount(): Result<Int> {
        try {
            val result = api._getNotificationCount()
            return Result.Success(result.count.toInt())
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun clearToken(): Result<Unit> {
        storage.accessToken.clear()
        return Result.Success(Unit)
    }
}
