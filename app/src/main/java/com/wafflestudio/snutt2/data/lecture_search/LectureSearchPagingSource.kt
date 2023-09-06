package com.wafflestudio.snutt2.data.lecture_search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.PostSearchQueryParams
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.toCreditNumber
import com.wafflestudio.snutt2.model.SearchTimeDto
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.model.TagType

class LectureSearchPagingSource(
    private val api: SNUTTRestApi,
    year: Long,
    semester: Long,
    title: String,
    tags: List<TagDto>,
    times: List<SearchTimeDto>?,
    timesToExclude: List<SearchTimeDto>?,
) : PagingSource<Long, LectureDto>() {

    private val queryParam: PostSearchQueryParams = PostSearchQueryParams(
        year = year,
        semester = semester,
        title = title,
        classification = tags.extractTagString(TagType.CLASSIFICATION),
        credit = tags.extractTagString(TagType.CREDIT).map { it.toCreditNumber() },
        academic_year = tags.extractTagString(TagType.ACADEMIC_YEAR),
        instructor = tags.extractTagString(TagType.INSTRUCTOR),
        department = tags.extractTagString(TagType.DEPARTMENT),
        category = tags.extractTagString(TagType.CATEGORY),
        times = times,
        timesToExclude = timesToExclude,
        etc = tags.mapNotNull {
            when (it) {
                TagDto.ETC_ENG -> "E"
                TagDto.ETC_MILITARY -> "MO"
                else -> null
            }
        }.ifEmpty { null },
        offset = null,
        limit = null
    )

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, LectureDto> {
        val offset = params.key ?: LECTURE_SEARCH_STARTING_PAGE_INDEX

        return try {
            val response = api._postSearchQuery(
                queryParam.copy(
                    offset = offset,
                    limit = params.loadSize.toLong()
                )
            )
            LoadResult.Page(
                data = response,
                prevKey = if (offset == LECTURE_SEARCH_STARTING_PAGE_INDEX) null else offset - params.loadSize,
                nextKey = if (response.isEmpty()) null else offset + params.loadSize
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, LectureDto>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    private fun List<TagDto>.extractTagString(type: TagType): List<String> {
        return filter { it.type == type }.map { it.name }
    }

    companion object {
        const val LECTURE_SEARCH_STARTING_PAGE_INDEX: Long = 0
    }
}
