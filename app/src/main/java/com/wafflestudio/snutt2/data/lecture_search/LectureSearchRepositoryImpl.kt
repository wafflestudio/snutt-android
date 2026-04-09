package com.wafflestudio.snutt2.data.lecture_search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.wafflestudio.snutt2.storage.SNUTTStorage
import com.wafflestudio.snutt2.data.mapper.toDto
import com.wafflestudio.snutt2.data.mapper.toSearchedLecture
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.SearchTag
import com.wafflestudio.snutt2.domainmodel.SearchTime
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.domainmodel.TagType
import com.wafflestudio.snutt2.network.api.SNUTTRestApi
import com.wafflestudio.snutt2.network.dto.TagDto
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
        courseBook: CourseBook,
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
                    year = courseBook.year,
                    semester = courseBook.semester,
                    title = title,
                    tags = tags.map { it.toDto() },
                    times = times?.map { it.toDto() },
                    timesToExclude = timesToExclude?.map { it.toDto() },
                )
            },
        ).flow.map { pagingData -> pagingData.map { it.toSearchedLecture() } }
    }

    override suspend fun getSearchTags(courseBook: CourseBook): List<SearchTag> {
        val response = api._getTagList(courseBook.year.toInt(), courseBook.semester.toInt())
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

    companion object {
        const val LECTURES_LOAD_PAGE_SIZE = 30
    }
}
