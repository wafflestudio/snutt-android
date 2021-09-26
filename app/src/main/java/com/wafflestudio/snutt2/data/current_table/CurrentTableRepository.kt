package com.wafflestudio.snutt2.data.current_table

import com.wafflestudio.snutt2.lib.network.dto.PostCustomLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PutLectureParams
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import kotlinx.coroutines.flow.Flow

interface CurrentTableRepository {
    val currentTable: Flow<TableDto>

    suspend fun addLecture(lectureId: String)

    suspend fun removeLecture(lectureId: String)

    // FIXME: do not expose network layer data class
    suspend fun createCustomLecture(lecture: PostCustomLectureParams)

    suspend fun resetLecture(lectureId: String)

    // FIXME: do not expose network layer data class
    suspend fun updateLecture(lectureId: String, target: PutLectureParams)

    suspend fun getLectureSyllabusUrl(
        courseNumber: String,
        lectureNumber: String
    ): String
}
