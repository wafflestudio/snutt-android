package com.wafflestudio.snutt2.data.semester_status

import com.wafflestudio.snutt2.domainmodel.SemesterStatus
import com.wafflestudio.snutt2.lib.network.Result
import kotlinx.coroutines.flow.StateFlow

interface SemesterStatusRepository {
    val semesterStatus: StateFlow<SemesterStatus?>

    suspend fun fetchSemesterStatus(): Result<Unit>
}
