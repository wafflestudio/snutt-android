package com.wafflestudio.snutt2.data.current_table

import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.network.dto.PostCustomLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PutLectureParams
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface CurrentTableRepository {
    val currentTable: StateFlow<TableDto?>

    val previewTheme: Flow<TimetableColorTheme?>

    suspend fun addLecture(lectureId: String, isForced: Boolean)

    suspend fun removeLecture(lectureId: String)

    // FIXME: do not expose network layer data class
    suspend fun createCustomLecture(lecture: PostCustomLectureParams)

    suspend fun resetLecture(lectureId: String)

    // FIXME: do not expose network layer data class
    suspend fun updateLecture(lectureId: String, target: PutLectureParams)

    suspend fun setPreviewTheme(previewTheme: TimetableColorTheme?)

    suspend fun getLectureSyllabusUrl(
        courseNumber: String,
        lectureNumber: String
    ): String
}
