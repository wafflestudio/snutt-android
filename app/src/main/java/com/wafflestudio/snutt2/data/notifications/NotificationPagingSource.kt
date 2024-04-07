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
        TODO("Not yet implemented")
    }

    override fun getRefreshKey(state: PagingState<Int, NotificationDto>): Int? {
        TODO("Not yet implemented")
    }
}
