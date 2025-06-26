package com.wafflestudio.snutt2.data.lecture_diary

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.domainmodel.DiaryWrite
import com.wafflestudio.snutt2.domainmodel.preview.DiaryPreviewData
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.Unknown
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiaryRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
) :
    DiaryRepository {
    override fun getDiaryWriteInit(): Result<DiaryWrite> {
        return Result.Success(DiaryPreviewData.diaryWriteInit)
    }

    override suspend fun saveDiaryWrite(diaryWriteData: DiaryWrite): Result<Unit> {
        return Result.Fail(Unknown("")) // good
    }

    override suspend fun clearToken(): Result<Unit> {
        storage.accessToken.clear()
        return Result.Success(Unit)
    }
}
