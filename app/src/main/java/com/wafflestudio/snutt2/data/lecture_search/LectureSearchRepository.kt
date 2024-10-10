package com.wafflestudio.snutt2.data.lecture_search

import androidx.paging.PagingData
import com.wafflestudio.snutt2.lib.network.dto.core.LectureBuildingDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.model.SearchTimeDto
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.model.TagType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface LectureSearchRepository {

    val recentSearchedDepartments: StateFlow<List<TagDto>>

    fun getLectureSearchResultStream(
        year: Long,
        semester: Long,
        title: String,
        tags: List<TagDto>,
        times: List<SearchTimeDto>?,
        timesToExclude: List<SearchTimeDto>?,
    ): Flow<PagingData<LectureDto>>

    suspend fun getSearchTags(year: Long, semester: Long): List<TagDto>

    suspend fun getBuildings(
        places: String,
    ): List<LectureBuildingDto>

    fun storeRecentSearchedDepartment(tag: TagDto)

    fun removeRecentSearchedDepartment(tag: TagDto)
}
