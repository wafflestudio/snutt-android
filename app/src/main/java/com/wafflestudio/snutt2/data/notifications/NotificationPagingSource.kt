package com.wafflestudio.snutt2.data.notifications

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wafflestudio.snutt2.core.data.toTempModel
import com.wafflestudio.snutt2.core.network.SNUTTNetworkDataSource
import com.wafflestudio.snutt2.data.toExternalModel
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import javax.inject.Inject

class NotificationPagingSource @Inject constructor(
    private val api: SNUTTNetworkDataSource,
) : PagingSource<Int, NotificationDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationDto> {
        val offset = params.key ?: NOTIFICATION_STARTING_PAGE_INDEX
        return try {
            val response = api._getNotification(
                limit = params.loadSize,
                offset = offset,
                explicit = 1,
            ).toTempModel().toExternalModel() // TODO : 변환 함수 사용 부분
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
