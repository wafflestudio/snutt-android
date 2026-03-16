package com.wafflestudio.snutt2.data.current_table

import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.domainmodel.Table
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.dto.PostCustomLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PutLectureParams
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureReviewDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import kotlinx.coroutines.flow.StateFlow

interface CurrentTableRepository {

    val isVisitedSessionlessLectureList: StateFlow<Boolean>
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

    suspend fun getBookmarksOfSemester(year: Long, semester: Long): List<LectureDto>

    suspend fun addBookmark(lecture: LectureDto): Result<Unit>

    suspend fun deleteBookmark(lecture: LectureDto): Result<Unit>

    // FIXME: do not expose network layer data class
    suspend fun getLectureReviewSummary(
        lectureId: String,
    ): LectureReviewDto

    suspend fun updateCurrentTable()

    // 여기부터 리팩토링 코드
    val currentTableRefactored: StateFlow<Table?>

    suspend fun visitSessionlessLectureList()

    suspend fun getBookmarksNew(): Result<List<SearchedLecture>>

    suspend fun addBookmarkNew(lecture: Lecture): Result<Unit>

    suspend fun deleteBookmark(lecture: Lecture): Result<Unit>

    suspend fun addLectureNew(lectureId: String, isForced: Boolean): Result<Unit>

    suspend fun removeLectureNew(lectureId: String): Result<Unit>

    suspend fun removeLectureNewNew(lecture: SearchedLecture): Result<Unit>

    suspend fun updateLectureNew(lecture: Lecture, isForced: Boolean): Result<Unit>

    suspend fun resetLectureNew(lectureId: String): Result<LocalLecture>

    suspend fun getSyllabusUrlNew(courseNumber: String, lectureNumber: String): Result<String>

    suspend fun getReviewInfoNew(lectureId: String): Result<LectureReviewInfo?>
}
