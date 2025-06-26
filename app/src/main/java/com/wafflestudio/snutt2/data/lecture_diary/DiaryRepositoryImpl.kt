package com.wafflestudio.snutt2.data.lecture_diary

import com.wafflestudio.snutt2.domainmodel.DiaryWrite
import com.wafflestudio.snutt2.domainmodel.preview.DiaryPreviewData
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.Unknown
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiaryRepositoryImpl @Inject constructor(private val api: SNUTTRestApi) :
    DiaryRepository {
    override fun getDiaryWriteInit(): Result<DiaryWrite> {
        return Result.Success(DiaryPreviewData.diaryWriteInit)
    }

    override suspend fun saveDiaryWrite(diaryWriteData: DiaryWrite): Result<DiaryWrite> {
        return Result.Fail(Unknown("")) // good
    }
}
