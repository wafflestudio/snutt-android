package com.wafflestudio.snutt2.data.lecture_diary

import com.wafflestudio.snutt2.domainmodel.DiaryWriteInit
import kotlinx.coroutines.flow.Flow

interface DiaryRepository {
    fun getDiaryWriteInit(): Flow<DiaryWriteInit>

    fun saveDiaryWrite()
}
