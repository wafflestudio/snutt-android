package com.wafflestudio.snutt2.test

import com.wafflestudio.snutt2.lib.network.Result

interface TestRepository {
    suspend fun registerLocal(id: String, password: String, email: String): Result<Unit>

    suspend fun getNotificationCount(): Result<Int>

    suspend fun clearToken()
}
