package com.wafflestudio.snutt2.data.notifications

import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wafflestudio.snutt2.data.lecture_search.LectureSearchPagingSource.Companion.LECTURE_SEARCH_STARTING_PAGE_INDEX
import com.wafflestudio.snutt2.data.notifications.PreviewData.sampleNotifications
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
            var response = api._getNotification(
                limit = limit,
                offset = offset,
                explicit = 1
            )
            if(offset == 0 ) response = response + sampleNotifications
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
