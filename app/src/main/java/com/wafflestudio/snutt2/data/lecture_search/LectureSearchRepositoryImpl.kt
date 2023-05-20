package com.wafflestudio.snutt2.data.lecture_search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wafflestudio.snutt2.lib.SnuttUrls
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.model.LectureTime
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.model.TagType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LectureSearchRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val snuttUrls: SnuttUrls
) : LectureSearchRepository {

    override fun getLectureSearchResultStream(
        year: Long,
        semester: Long,
        title: String,
        tags: List<TagDto>,
        times: List<LectureTime>,
    ): Flow<PagingData<LectureDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = LECTURES_LOAD_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                LectureSearchPagingSource(
                    api,
                    year = year,
                    semester = semester,
                    title = title,
                    tags = tags,
                    times = times,
                )
            }
        ).flow
    }

    override suspend fun getLectureReviewUrl(courseNumber: String, instructor: String): String {
        val response = api._getLecturesId(courseNumber, instructor)
        return snuttUrls.getReviewDetail(response.id)
    }

    override suspend fun getSearchTags(year: Long, semester: Long): List<TagDto> {
        val response = api._getTagList(year.toInt(), semester.toInt())
        val list = mutableListOf<TagDto>()
        list.apply {
            addAll(response.department.map { TagDto(TagType.DEPARTMENT, it) })
            addAll(response.classification.map { TagDto(TagType.CLASSIFICATION, it) })
            addAll(response.academicYear.map { TagDto(TagType.ACADEMIC_YEAR, it) })
            addAll(response.credit.map { TagDto(TagType.CREDIT, it) })
            addAll(response.instructor.map { TagDto(TagType.INSTRUCTOR, it) })
            addAll(response.category.map { TagDto(TagType.CATEGORY, it) })
        }
        return list
    }

    companion object {
        const val LECTURES_LOAD_PAGE_SIZE = 30
    }
}
