package com.wafflestudio.snutt2.data.lecturesearch

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.wafflestudio.snutt2.data.mapper.toDto
import com.wafflestudio.snutt2.data.mapper.toSearchedLecture
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.SearchTag
import com.wafflestudio.snutt2.domain.model.SearchTime
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.domain.model.TagType
import com.wafflestudio.snutt2.network.api.SNUTTRestApi
import com.wafflestudio.snutt2.storage.SNUTTStorage
import com.wafflestudio.snutt2.storage.model.toDomainModel
import com.wafflestudio.snutt2.storage.model.toLocalEntity
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
        storage.recentSearchedDepartments.asStateFlow().map { entities ->
            entities.map { it.toDomainModel() }
        }

    override fun getLectureSearchResultStream(
        courseBook: CourseBook,
        title: String,
        tags: List<SearchTag>,
        times: List<SearchTime>?,
        timesToExclude: List<SearchTime>?,
    ): Flow<PagingData<SearchedLecture>> = Pager(
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
        val entity = tag.toLocalEntity()
        val previousStoredTags = storage.recentSearchedDepartments.get()
        storage.recentSearchedDepartments.update(
            (previousStoredTags.filter { it != entity } + entity).takeLast(5),
        )
    }

    override fun removeRecentSearchedDepartment(tag: SearchTag) {
        check(tag is SearchTag.Regular)
        val entity = tag.toLocalEntity()
        val previousStoredTags = storage.recentSearchedDepartments.get()
        storage.recentSearchedDepartments.update(previousStoredTags - entity)
    }

    companion object {
        const val LECTURES_LOAD_PAGE_SIZE = 30
    }
}
