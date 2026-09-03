package com.wafflestudio.snutt2.domain

import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.data.tables.TableRepository
import javax.inject.Inject

class EnsureCurrentTableLoadedUseCase @Inject constructor(
    private val tableRepository: TableRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        var lastFailure: Result.Fail? = null

        repeat(MAX_ATTEMPTS) {
            when (val result = loadCurrentTable()) {
                is Result.Success -> return result
                is Result.Fail -> lastFailure = result
            }
        }

        return checkNotNull(lastFailure)
    }

    private suspend fun loadCurrentTable(): Result<Unit> {
        val lastViewedTableId = tableRepository.currentTable.value?.summary?.id
        if (lastViewedTableId != null) {
            when (val result = tableRepository.fetchAndSelectTable(lastViewedTableId)) {
                is Result.Success -> return result
                is Result.Fail -> Unit
            }
        }

        return tableRepository.fetchAndSelectDefaultTable()
    }

    private companion object {
        const val MAX_ATTEMPTS = 2
    }
}
