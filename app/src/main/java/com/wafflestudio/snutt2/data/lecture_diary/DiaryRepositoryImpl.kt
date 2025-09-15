package com.wafflestudio.snutt2.data.lecture_diary

import com.wafflestudio.snutt2.domainmodel.diary.DiaryActivity
import com.wafflestudio.snutt2.domainmodel.diary.DiaryAnsweredQuestion
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiaryRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
) : DiaryRepository {
    override suspend fun saveDiaryWrite(
        lectureId: String,
        activities: List<DiaryActivity>,
        questionAnswers: List<DiaryAnsweredQuestion>,
        comment: String,
    ): Result<Unit> {
        return Result.Success(Unit)
    }
}
