package com.wafflestudio.snutt2.data.course_books

import com.wafflestudio.snutt2.model.SemesterStatus
import kotlinx.coroutines.flow.Flow

interface SemesterStatusRepository {
    val semesterStatus: Flow<SemesterStatus>

    suspend fun fetchSemesterStatus()
}
