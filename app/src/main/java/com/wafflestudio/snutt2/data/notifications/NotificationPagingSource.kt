package com.wafflestudio.snutt2.data.notifications

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wafflestudio.snutt2.data.lecture_search.LectureSearchPagingSource.Companion.LECTURE_SEARCH_STARTING_PAGE_INDEX
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import javax.inject.Inject

class NotificationPagingSource @Inject constructor(
    private val api: SNUTTRestApi,
) : PagingSource<Int, NotificationDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationDto> {
        val offset = params.key ?: NOTIFICATION_OFFSET
        val limit = params.loadSize
        return try {
            val response = api._getNotification(
                limit = limit,
                offset = offset,
                explicit = 0 //TODO: (0이 아니면 읽음 상태를 업데이트)
            )
            LoadResult.Page(
                data = response,
                prevKey = if (offset == NOTIFICATION_OFFSET) null else offset - params.loadSize,
                nextKey = if (response.isEmpty()) null else offset + params.loadSize,
            )
        }
        catch(e: Exception){
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, NotificationDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    companion object {
        const val NOTIFICATION_OFFSET: Int = 0
    }
}
