package com.wafflestudio.snutt2.data.lecture_search

import androidx.paging.PagingData
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.model.TagDto
import kotlinx.coroutines.flow.Flow

interface LectureSearchRepository {
    suspend fun getLectureSearchResultStream(
        year: Long,
        semester: Long,
        title: String,
        tags: List<TagDto>,
        lecturesMask: List<Int>?
    ): Flow<PagingData<LectureDto>>

    suspend fun getSearchTags(year: Long, semester: Long): List<TagDto>
}
