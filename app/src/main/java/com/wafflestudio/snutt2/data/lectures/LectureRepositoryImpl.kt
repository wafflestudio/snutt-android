package com.wafflestudio.snutt2.data.lectures

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.model.TagDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LectureRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi
) : LectureRepository {

    override suspend fun getLectureResultStream(
        year: Long,
        semester: Long,
        title: String,
        tags: List<TagDto>,
        lecturesMask: List<Int>?
    ): Flow<PagingData<LectureDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = LECTURES_LOAD_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                LecturePagingSource(
                    api,
                    year = year,
                    semester = semester,
                    title = title,
                    tags = tags,
                    lecturesMask = lecturesMask
                )
            }
        ).flow
    }

    companion object {
        const val LECTURES_LOAD_PAGE_SIZE = 30
    }
}
