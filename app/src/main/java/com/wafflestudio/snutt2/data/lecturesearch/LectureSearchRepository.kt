package com.wafflestudio.snutt2.data.lecturesearch

import androidx.paging.PagingData
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.SearchTag
import com.wafflestudio.snutt2.domain.model.SearchTime
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import kotlinx.coroutines.flow.Flow

interface LectureSearchRepository {

    val recentSearchedDepartmentTags: Flow<List<SearchTag>>

    fun getLectureSearchResultStream(
        courseBook: CourseBook,
        title: String,
        tags: List<SearchTag>,
        times: List<SearchTime>?,
        timesToExclude: List<SearchTime>?,
    ): Flow<PagingData<SearchedLecture>>

    suspend fun getSearchTags(courseBook: CourseBook): List<SearchTag>

    fun storeRecentSearchedDepartment(tag: SearchTag)

    fun removeRecentSearchedDepartment(tag: SearchTag)
}
