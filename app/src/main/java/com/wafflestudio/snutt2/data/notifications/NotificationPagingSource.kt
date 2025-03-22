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
        TODO()
    }

    override fun getRefreshKey(state: PagingState<Int, NotificationDto>): Int? {
        TODO()
    }
}
