package com.wafflestudio.snutt2.data.notifications

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import javax.inject.Inject

class NotificationPagingSource @Inject constructor(
    private val api: SNUTTRestApi,
) : PagingSource<Int, NotificationDto>() {

    // TODO: https://developer.android.com/codelabs/android-paging-basics#4
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationDto> {
        val offset = params.key ?: 0

        return try {
            val response = api._getNotification(
                explicit = 5, // TODO:
                limit = params.loadSize,
                offset = offset,
            )
            LoadResult.Page(
                data = response,
                prevKey = if (offset == 0) null else offset - params.loadSize,
                nextKey = if (response.isEmpty()) null else offset + params.loadSize,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, NotificationDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
        // TODO: 얘는 뭐하는 애일까...
    }
}
