package com.wafflestudio.snutt2.domain

import com.wafflestudio.snutt2.data.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class InitializeHomeUseCase @Inject constructor(
    private val ensureCurrentTableLoadedUseCase: EnsureCurrentTableLoadedUseCase,
    private val refreshInitialDataUseCase: RefreshInitialDataUseCase,
    private val externalScope: CoroutineScope,
) {
    suspend operator fun invoke(): Boolean {
        val refreshJob = externalScope.launch { refreshInitialDataUseCase() }

        val canEnterHome = withTimeoutOrNull(CURRENT_TABLE_LOADING_TIMEOUT_MILLIS) {
            ensureCurrentTableLoadedUseCase() is Result.Success
        } ?: false

        if (!canEnterHome) {
            refreshJob.cancel()
        }

        return canEnterHome
    }

    private companion object {
        const val CURRENT_TABLE_LOADING_TIMEOUT_MILLIS = 10_000L
    }
}
