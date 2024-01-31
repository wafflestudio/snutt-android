package com.wafflestudio.snutt2.data.current_table

import com.wafflestudio.snutt2.lib.network.dto.PostCustomLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PutLectureParams
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import kotlinx.coroutines.flow.StateFlow

interface CurrentTableRepository {
    val currentTable: StateFlow<TableDto?>

    suspend fun addLecture(lectureId: String, isForced: Boolean)

    suspend fun removeLecture(lectureId: String)

    // FIXME: do not expose network layer data class
    suspend fun createCustomLecture(lecture: PostCustomLectureParams)

    suspend fun resetLecture(lectureId: String): LectureDto

    // FIXME: do not expose network layer data class
    suspend fun updateLecture(lectureId: String, target: PutLectureParams)

    suspend fun getLectureSyllabusUrl(
        courseNumber: String,
        lectureNumber: String,
    ): String

    suspend fun getBookmarks(): List<LectureDto>

    suspend fun addBookmark(lecture: LectureDto)

    suspend fun deleteBookmark(lecture: LectureDto)
}
