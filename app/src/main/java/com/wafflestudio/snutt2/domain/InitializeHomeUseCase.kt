package com.wafflestudio.snutt2.domain

import com.wafflestudio.snutt2.data.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import javax.inject.Inject

class InitializeHomeUseCase @Inject constructor(
    private val ensureCurrentTableLoadedUseCase: EnsureCurrentTableLoadedUseCase,
    private val refreshInitialDataUseCase: RefreshInitialDataUseCase,
    private val externalScope: CoroutineScope,
) {
    suspend operator fun invoke(): Boolean {
        val refreshJob = externalScope.launch { refreshInitialDataUseCase() }

        return when (ensureCurrentTableLoadedUseCase()) {
            is Result.Success -> true
            is Result.Fail -> {
                refreshJob.cancelAndJoin()
                false
            }
        }
    }
}
