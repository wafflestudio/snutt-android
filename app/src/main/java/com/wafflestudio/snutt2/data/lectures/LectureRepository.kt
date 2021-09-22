package com.wafflestudio.snutt2.data.lectures

import androidx.paging.PagingData
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.model.TagDto
import kotlinx.coroutines.flow.Flow

interface LectureRepository {
    suspend fun getLectureResultStream(
        year: Long,
        semester: Long,
        title: String,
        tags: List<TagDto>,
        lecturesMask: List<Int>?
    ): Flow<PagingData<LectureDto>>
}
