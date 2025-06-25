package com.wafflestudio.snutt2.data.lecture_diary

import com.wafflestudio.snutt2.domainmodel.DiaryWrite
import kotlinx.coroutines.flow.Flow

interface DiaryRepository {
    fun getDiaryWriteInit(): Flow<DiaryWrite>

    fun saveDiaryWrite()
}
