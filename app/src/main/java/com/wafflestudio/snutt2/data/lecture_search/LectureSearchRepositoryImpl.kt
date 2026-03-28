package com.wafflestudio.snutt2.data.lecture_search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.domainmodel.SearchTag
import com.wafflestudio.snutt2.domainmodel.SearchTime
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.LectureBuildingDto
import com.wafflestudio.snutt2.lib.network.toDomainError
import com.wafflestudio.snutt2.model.SearchTimeDto
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.model.TagType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LectureSearchRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
) : LectureSearchRepository {

    override val recentSearchedDepartmentTags: Flow<List<SearchTag>> =
        storage.recentSearchedDepartments.asStateFlow().map { dtos ->
            dtos.map { SearchTag.Regular(it.type, it.name) }
        }

    override fun getLectureSearchResultStream(
        year: Long,
        semester: Long,
        title: String,
        tags: List<SearchTag>,
        times: List<SearchTime>?,
        timesToExclude: List<SearchTime>?,
    ): Flow<PagingData<SearchedLecture>> {
        return Pager(
            config = PagingConfig(
                pageSize = LECTURES_LOAD_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                LectureSearchPagingSource(
                    api,
                    year = year,
                    semester = semester,
                    title = title,
                    tags = tags.map { it.toTagDto() },
                    times = times?.map { it.toSearchTimeDto() },
                    timesToExclude = timesToExclude?.map { it.toSearchTimeDto() },
                )
            },
        ).flow.map { pagingData -> pagingData.map { it.toSearchedLecture() } }
    }

    override suspend fun getSearchTags(year: Long, semester: Long): List<SearchTag> {
        val response = api._getTagList(year.toInt(), semester.toInt())
        val list = mutableListOf<SearchTag>()
        list.apply {
            addAll(response.department.map { SearchTag.Regular(TagType.DEPARTMENT, it) })
            addAll(response.classification.map { SearchTag.Regular(TagType.CLASSIFICATION, it) })
            addAll(response.academicYear.map { SearchTag.Regular(TagType.ACADEMIC_YEAR, it) })
            addAll(response.credit.map { SearchTag.Regular(TagType.CREDIT, it) })
            addAll(response.category.map { SearchTag.Regular(TagType.CATEGORY, it) })
            addAll(response.categoryPre2025.map { SearchTag.Regular(TagType.CATEGORY_PRE2025, it) })
            addAll(response.sortCriteria.map { SearchTag.Regular(TagType.SORT_CRITERIA, it) })
        }
        return list
    }

    override fun storeRecentSearchedDepartment(tag: SearchTag) {
        check(tag is SearchTag.Regular)
        val tagDto = TagDto(tag.type, tag.name)
        val previousStoredTags = storage.recentSearchedDepartments.get()
        storage.recentSearchedDepartments.update(
            (previousStoredTags.filter { it != tagDto } + tagDto).takeLast(5),
        )
    }

    override fun removeRecentSearchedDepartment(tag: SearchTag) {
        check(tag is SearchTag.Regular)
        val tagDto = TagDto(tag.type, tag.name)
        val previousStoredTags = storage.recentSearchedDepartments.get()
        storage.recentSearchedDepartments.update(previousStoredTags - tagDto)
    }

    override suspend fun getBuildings(places: List<String>): Result<List<LectureBuildingDto>> {
        val joined = places.joinToString(",")
        if (joined.isBlank()) return Result.Success(emptyList())
        try {
            val response = api._getBuildings(joined)
            return Result.Success(response.content)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getSyllabusUrl(
        courseBook: CourseBook,
        courseNumber: String,
        lectureNumber: String,
    ): Result<String> {
        try {
            val url = api._getCoursebooksOfficial(courseBook.year, courseBook.semester, courseNumber, lectureNumber).url
            return Result.Success(url)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getReviewInfo(lectureId: String): Result<LectureReviewInfo?> {
        try {
            val dto = api._getLectureReviewSummary(lectureId)
            return Result.Success(LectureReviewInfo(id = dto.id, rating = dto.rating, reviewCount = dto.reviewCount ?: 0))
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    companion object {
        const val LECTURES_LOAD_PAGE_SIZE = 30
    }
}

private fun SearchTag.toTagDto(): TagDto = when (this) {
    is SearchTag.Regular -> TagDto(type, name)
    SearchTag.TimeEmpty -> TagDto.TIME_EMPTY
    SearchTag.TimeSelect -> TagDto.TIME_SELECT
    SearchTag.EtcEng -> TagDto.ETC_ENG
    SearchTag.EtcMilitary -> TagDto.ETC_MILITARY
}

private fun SearchTime.toSearchTimeDto() = SearchTimeDto(
    // NOTE: DayOfWeek는 1이 월요일이고, 서버는 0이 월요일이다
    day = day.value - 1,
    startMinute = startTime.hour * 60 + startTime.minute,
    endMinute = endTime.hour * 60 + endTime.minute,
)
