package com.wafflestudio.snutt2.data.lecture_diary

import com.wafflestudio.snutt2.domainmodel.DiaryWrite
import com.wafflestudio.snutt2.domainmodel.preview.DiaryPreviewData
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiaryRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
) :
    DiaryRepository {
    override fun getDiaryWriteInit(): Result<DiaryWrite> {
        return Result.Success(DiaryPreviewData.diaryWriteNewInit)
    }

    override fun getTodayWrittenLectures(): Result<List<String>> {
        return Result.Success(listOf("686e8d3c2afaf11b888e2722"))
    }

    override suspend fun saveDiaryWrite(diaryWriteData: DiaryWrite): Result<Unit> {
        return Result.Success(Unit) // good
    }
}
