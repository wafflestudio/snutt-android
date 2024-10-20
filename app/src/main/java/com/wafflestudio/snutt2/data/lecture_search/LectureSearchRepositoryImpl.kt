package com.wafflestudio.snutt2.data.lecture_search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wafflestudio.snutt2.lib.SnuttUrls
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.LectureBuildingDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.model.SearchTimeDto
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.model.TagType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LectureSearchRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val snuttUrls: SnuttUrls,
) : LectureSearchRepository {

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
            addAll(response.sortCriteria.map { TagDto(TagType.SORT_CRITERIA, it) })
        }
        return list
    }

    override suspend fun getBuildings(places: String): List<LectureBuildingDto> {
        val response = api._getBuildings(places)
        return response.content
    }

    companion object {
        const val LECTURES_LOAD_PAGE_SIZE = 30
    }
}
