package com.wafflestudio.snutt2.fake

import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.data.semesterstatus.SemesterStatusRepository
import com.wafflestudio.snutt2.domain.model.SemesterStatus
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSemesterStatusRepository : SemesterStatusRepository {
    override val semesterStatus = MutableStateFlow<SemesterStatus?>(null)

    var fetchSemesterStatusResult: Result<Unit> = Result.Success(Unit)
    var fetchSemesterStatusDeferred: CompletableDeferred<Result<Unit>>? = null
    var fetchSemesterStatusCallCount = 0
        private set

    override suspend fun fetchSemesterStatus(): Result<Unit> {
        fetchSemesterStatusCallCount++
        return fetchSemesterStatusDeferred?.await() ?: fetchSemesterStatusResult
    }
}
