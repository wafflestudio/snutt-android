package com.wafflestudio.snutt2.data.lecture_diary

import com.wafflestudio.snutt2.domainmodel.DiaryWrite
import com.wafflestudio.snutt2.lib.network.Result

interface DiaryRepository {
    fun getDiaryWriteInit(): Result<DiaryWrite>

    fun getTodayWrittenLectures(): Result<List<String>>

    suspend fun saveDiaryWrite(diaryWriteData: DiaryWrite): Result<Unit>
}
