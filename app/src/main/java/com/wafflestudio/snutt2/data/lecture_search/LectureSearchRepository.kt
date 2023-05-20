package com.wafflestudio.snutt2.data.lecture_search

import androidx.paging.PagingData
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.model.LectureTime
import com.wafflestudio.snutt2.model.TagDto
import kotlinx.coroutines.flow.Flow

interface LectureSearchRepository {

    fun getLectureSearchResultStream(
        year: Long,
        semester: Long,
        title: String,
        tags: List<TagDto>,
        times: List<LectureTime>,
    ): Flow<PagingData<LectureDto>>

    suspend fun getLectureReviewUrl(
        courseNumber: String,
        instructor: String,
    ): String

    suspend fun getSearchTags(year: Long, semester: Long): List<TagDto>
}
