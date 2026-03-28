package com.wafflestudio.snutt2.data.current_table

import com.wafflestudio.snutt2.domainmodel.CustomLecture
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.lib.network.Result

interface CurrentTableLectureRepository {

    suspend fun addLecture(lectureId: String, isForced: Boolean): Result<Unit>

    suspend fun removeLecture(lectureId: String): Result<Unit>

    suspend fun removeLecture(lecture: SearchedLecture): Result<Unit>

    suspend fun updateLecture(lecture: Lecture, isForced: Boolean): Result<Unit>

    suspend fun resetLecture(lectureId: String): Result<LocalLecture>

    suspend fun createCustomLecture(lecture: CustomLecture, isForced: Boolean = false): Result<Unit>
}
