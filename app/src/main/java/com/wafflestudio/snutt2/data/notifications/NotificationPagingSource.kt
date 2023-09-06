package com.wafflestudio.snutt2.data.notifications

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import javax.inject.Inject

class NotificationPagingSource @Inject constructor(
    private val api: SNUTTRestApi,
) : PagingSource<Int, NotificationDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationDto> {
        val offset = params.key ?: NOTIFICATION_STARTING_PAGE_INDEX
        return try {
            val response = api._getNotification(
                limit = params.loadSize,
                offset = offset,
                explicit = 1,
            )
            LoadResult.Page(
                data = response,
                prevKey = if (offset == NOTIFICATION_STARTING_PAGE_INDEX) null else offset - params.loadSize,
                nextKey = if (response.isEmpty()) null else offset + params.loadSize,
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, NotificationDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    companion object {
        const val NOTIFICATION_STARTING_PAGE_INDEX = 0
    }
}
