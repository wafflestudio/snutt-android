package com.wafflestudio.snutt2.data.current_table

import com.wafflestudio.snutt2.domainmodel.CustomLecture
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.domainmodel.Table
import com.wafflestudio.snutt2.lib.network.Result
import kotlinx.coroutines.flow.StateFlow

interface CurrentTableRepository {

    val isVisitedSessionlessLectureList: StateFlow<Boolean>

    val currentTable: StateFlow<Table?>

    suspend fun updateCurrentTable()

    suspend fun visitSessionlessLectureList()

    suspend fun addLecture(lectureId: String, isForced: Boolean): Result<Unit>

    suspend fun removeLecture(lectureId: String): Result<Unit>

    suspend fun removeLecture(lecture: SearchedLecture): Result<Unit>

    suspend fun updateLecture(lecture: Lecture, isForced: Boolean): Result<Unit>

    suspend fun resetLecture(lectureId: String): Result<LocalLecture>

    suspend fun getSyllabusUrl(courseNumber: String, lectureNumber: String): Result<String>

    suspend fun getReviewInfo(lectureId: String): Result<LectureReviewInfo?>

    suspend fun createCustomLecture(lecture: CustomLecture, isForced: Boolean = false): Result<Unit>
}
