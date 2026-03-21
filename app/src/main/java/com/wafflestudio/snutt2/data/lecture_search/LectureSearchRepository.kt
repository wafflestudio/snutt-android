package com.wafflestudio.snutt2.data.lecture_search

import androidx.paging.PagingData
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.domainmodel.SearchTag
import com.wafflestudio.snutt2.domainmodel.SearchTime
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.dto.core.LectureBuildingDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.model.SearchTimeDto
import com.wafflestudio.snutt2.model.TagDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface LectureSearchRepository {

    val recentSearchedDepartments: StateFlow<List<TagDto>>

    val firstBookmarkAlert: StateFlow<Boolean>

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

    // region Domain model (SearchTag-based)
    val recentSearchedDepartmentTags: Flow<List<SearchTag>>

    fun getLectureSearchResultStreamNew(
        year: Long,
        semester: Long,
        title: String,
        tags: List<SearchTag>,
        times: List<SearchTime>?,
        timesToExclude: List<SearchTime>?,
    ): Flow<PagingData<SearchedLecture>>

    suspend fun getSearchTagsDomain(year: Long, semester: Long): List<SearchTag>
    fun storeRecentSearchedDepartment(tag: SearchTag)

    fun removeRecentSearchedDepartment(tag: SearchTag)

    // endregion

    fun setFirstBookmarkAlertShown()

    suspend fun getBuildingsNew(places: List<String>): Result<List<LectureBuildingDto>>

    suspend fun getSyllabusUrl(courseBook: CourseBook, courseNumber: String, lectureNumber: String): Result<String>

    suspend fun getReviewInfo(lectureId: String): Result<LectureReviewInfo?>
}
