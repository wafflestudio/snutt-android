package com.wafflestudio.snutt2.data.lecture_diary

import com.wafflestudio.snutt2.domainmodel.DiaryWrite
import com.wafflestudio.snutt2.lib.network.Result

interface DiaryRepository {
    fun getDiaryWriteInit(): Result<DiaryWrite>

    suspend fun saveDiaryWrite(diaryWriteData: DiaryWrite): Result<Unit>

    suspend fun clearToken(): Result<Unit>
}
