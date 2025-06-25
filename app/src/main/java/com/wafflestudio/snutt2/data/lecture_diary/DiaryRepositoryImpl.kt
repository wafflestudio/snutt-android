package com.wafflestudio.snutt2.data.lecture_diary

import com.wafflestudio.snutt2.domainmodel.DiaryWrite
import com.wafflestudio.snutt2.domainmodel.preview.DiaryPreviewData
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiaryRepositoryImpl @Inject constructor(private val api: SNUTTRestApi) :
    DiaryRepository {
    override fun getDiaryWriteInit(): Flow<DiaryWrite> {
        return flowOf(DiaryPreviewData.diaryWriteInit)
    }

    override fun saveDiaryWrite() {
    }
}
