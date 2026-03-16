package com.wafflestudio.snutt2.data.lecture_search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.data.lecture_cache.LectureCache
import com.wafflestudio.snutt2.domainmodel.SearchTag
import com.wafflestudio.snutt2.domainmodel.SearchTime
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.LectureBuildingDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
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
    private val lectureCache: LectureCache,
) : LectureSearchRepository {

    // region Legacy (TagDto-based)
    override val recentSearchedDepartments = storage.recentSearchedDepartments.asStateFlow()

    override val firstBookmarkAlert = storage.firstBookmarkAlert.asStateFlow()

    override fun getLectureSearchResultStream(
        year: Long,
        semester: Long,
        title: String,
        tags: List<TagDto>,
        times: List<SearchTimeDto>?,
        timesToExclude: List<SearchTimeDto>?,
    ): Flow<PagingData<LectureDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = LECTURES_LOAD_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                LectureSearchPagingSource(
                    api,
                    lectureCache,
                    year = year,
                    semester = semester,
                    title = title,
                    tags = tags,
                    times = times,
                    timesToExclude = timesToExclude,
                )
            },
        ).flow
    }

    override suspend fun getSearchTags(year: Long, semester: Long): List<TagDto> {
        val response = api._getTagList(year.toInt(), semester.toInt())
        val list = mutableListOf<TagDto>()
        list.apply {
            addAll(response.department.map { TagDto(TagType.DEPARTMENT, it) })
            addAll(response.classification.map { TagDto(TagType.CLASSIFICATION, it) })
            addAll(response.academicYear.map { TagDto(TagType.ACADEMIC_YEAR, it) })
            addAll(response.credit.map { TagDto(TagType.CREDIT, it) })
            addAll(response.category.map { TagDto(TagType.CATEGORY, it) })
            addAll(response.categoryPre2025.map { TagDto(TagType.CATEGORY_PRE2025, it) })
            addAll(response.sortCriteria.map { TagDto(TagType.SORT_CRITERIA, it) })
        }
        return list
    }

    override suspend fun getBuildings(places: String): List<LectureBuildingDto> {
        val response = api._getBuildings(places)
        return response.content
    }

    override fun storeRecentSearchedDepartment(tag: TagDto) {
        val previousStoredTags = storage.recentSearchedDepartments.get()
        storage.recentSearchedDepartments.update(
            (previousStoredTags.filter { it != tag } + tag).takeLast(5),
        )
    }

    override fun removeRecentSearchedDepartment(tag: TagDto) {
        val previousStoredTags = storage.recentSearchedDepartments.get()
        storage.recentSearchedDepartments.update(previousStoredTags - tag)
    }
    // endregion

    // region Domain model (SearchTag-based)
    override val recentSearchedDepartmentTags: Flow<List<SearchTag>> =
        storage.recentSearchedDepartments.asStateFlow().map { dtos ->
            dtos.map { SearchTag.Regular(it.type, it.name) }
        }

    override fun getLectureSearchResultStreamNew(
        year: Long,
        semester: Long,
        title: String,
        tags: List<SearchTag>,
        times: List<SearchTime>?,
        timesToExclude: List<SearchTime>?,
    ): Flow<PagingData<SearchedLecture>> {
        return getLectureSearchResultStream(
            year = year,
            semester = semester,
            title = title,
            tags = tags.map { it.toTagDto() },
            times = times?.map { it.toSearchTimeDto() },
            timesToExclude = timesToExclude?.map { it.toSearchTimeDto() },
        ).map { pagingData -> pagingData.map { it.toSearchedLecture() } }
    }

    override suspend fun getSearchTagsDomain(year: Long, semester: Long): List<SearchTag> {
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
        storeRecentSearchedDepartment(TagDto(tag.type, tag.name))
    }

    override fun removeRecentSearchedDepartment(tag: SearchTag) {
        check(tag is SearchTag.Regular)
        removeRecentSearchedDepartment(TagDto(tag.type, tag.name))
    }
    // endregion

    override fun setFirstBookmarkAlertShown() {
        storage.firstBookmarkAlert.update(false)
    }

    override suspend fun getBuildingsNew(places: List<String>): Result<List<LectureBuildingDto>> {
        val joined = places.joinToString(",")
        if (joined.isBlank()) return Result.Success(emptyList())
        try {
            val response = api._getBuildings(joined)
            return Result.Success(response.content)
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
