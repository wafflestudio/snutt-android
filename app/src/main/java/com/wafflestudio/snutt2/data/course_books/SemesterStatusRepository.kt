package com.wafflestudio.snutt2.data.course_books

import com.wafflestudio.snutt2.model.SemesterStatus
import kotlinx.coroutines.flow.StateFlow

interface SemesterStatusRepository {
    val semesterStatus: StateFlow<SemesterStatus>

    suspend fun fetchSemesterStatus()
}
