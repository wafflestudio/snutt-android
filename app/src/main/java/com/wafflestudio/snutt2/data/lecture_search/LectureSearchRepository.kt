package com.wafflestudio.snutt2.data.lecture_search

import androidx.paging.PagingData
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.SearchTag
import com.wafflestudio.snutt2.domainmodel.SearchTime
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
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
