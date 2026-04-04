package com.wafflestudio.snutt2.data.current_table_lecture

import com.wafflestudio.snutt2.domainmodel.CustomLecture
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.lib.network.Result

interface CurrentTableLectureRepository {

    suspend fun addLecture(lecture: SearchedLecture, isForced: Boolean): Result<Unit>

    suspend fun removeLecture(lecture: LocalLecture): Result<Unit>

    suspend fun removeLecture(lecture: SearchedLecture): Result<Unit>

    suspend fun updateLecture(lecture: Lecture, isForced: Boolean): Result<Unit>

    suspend fun resetLecture(lecture: LocalLecture): Result<LocalLecture>

    suspend fun createCustomLecture(lecture: CustomLecture, isForced: Boolean = false): Result<Unit>
}
