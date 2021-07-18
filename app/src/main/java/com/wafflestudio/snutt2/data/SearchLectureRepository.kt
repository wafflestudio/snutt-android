package com.wafflestudio.snutt2.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import androidx.paging.rxjava3.flowable
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.PostSearchQueryParams
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import com.wafflestudio.snutt2.lib.toCreditNumber
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.model.TagType
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchLectureRepository @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage
) {

    fun getPagingSource(title: String, tags: List<TagDto>): Pager<Long, LectureDto> {
        val current = storage.lastViewedTable.getValue().get()!!
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                prefetchDistance = 5,
            ),
            pagingSourceFactory = {
                SearchLecturePagingSource(
                    api,
                    current.year,
                    current.semester,
                    title,
                    tags
                )
            }
        )
    }
}

@Singleton
class SearchLecturePagingSource @Inject constructor(
    private val api: SNUTTRestApi,
    private val year: Long,
    private val semester: Long,
    private val title: String,
    private val tags: List<TagDto>
) : RxPagingSource<Long, LectureDto>() {

    override fun getRefreshKey(state: PagingState<Long, LectureDto>): Long? {
        return 0
    }

    override fun loadSingle(params: LoadParams<Long>): Single<LoadResult<Long, LectureDto>> {
        val offset = params.key ?: 0
        val loadSize = params.loadSize.toLong()

        return api.postSearchQuery(
            PostSearchQueryParams(
                year = year,
                semester = semester,
                title = title,
                classification = tags.extractTagString(TagType.CLASSIFICATION),
                credit = tags.extractTagString(TagType.CREDIT).map { it.toCreditNumber() },
                academic_year = tags.extractTagString(TagType.ACADEMIC_YEAR),
                instructor = tags.extractTagString(TagType.INSTRUCTOR),
                department = tags.extractTagString(TagType.DEPARTMENT),
                category = tags.extractTagString(TagType.CATEGORY),
                offset = offset,
                limit = loadSize
            )
        )
            .subscribeOn(Schedulers.io())
            .map { toLoadResult(it, offset = offset, loadSize = loadSize) }
            .onErrorReturn { LoadResult.Error(it) }
    }

    private fun List<TagDto>.extractTagString(type: TagType): List<String> {
        return filter { it.type == type }.map { it.name }
    }

    private fun toLoadResult(
        data: List<LectureDto>,
        offset: Long,
        loadSize: Long
    ): LoadResult<Long, LectureDto> {
        return LoadResult.Page(
            data = data,
            prevKey = if (offset == 0L) null else offset - loadSize,
            nextKey = if (data.isEmpty()) null else offset + loadSize
        )
    }
}
