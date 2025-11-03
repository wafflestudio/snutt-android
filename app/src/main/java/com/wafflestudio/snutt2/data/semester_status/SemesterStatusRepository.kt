package com.wafflestudio.snutt2.data.semester_status

import com.wafflestudio.snutt2.domainmodel.SemesterStatus
import kotlinx.coroutines.flow.StateFlow

interface SemesterStatusRepository {
    val semesterStatus: StateFlow<SemesterStatus?>

    suspend fun fetchSemesterStatus()
}
