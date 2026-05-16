package com.wafflestudio.snutt2.data.notifications

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wafflestudio.snutt2.network.api.SNUTTRestApi
import com.wafflestudio.snutt2.network.dto.NotificationDto
import kotlinx.coroutines.CancellationException
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
                prevKey = null,
                nextKey = if (response.size < params.loadSize) null else offset + response.size,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, NotificationDto>): Int? = null

    companion object {
        const val NOTIFICATION_STARTING_PAGE_INDEX = 0
    }
}
