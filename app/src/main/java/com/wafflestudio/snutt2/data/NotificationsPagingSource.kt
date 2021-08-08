package com.wafflestudio.snutt2.data

import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class NotificationsPagingSource @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage
) :
    RxPagingSource<Long, NotificationDto>() {

    override fun getRefreshKey(state: PagingState<Long, NotificationDto>): Long? {
        return 0
    }

    override fun loadSingle(params: LoadParams<Long>): Single<LoadResult<Long, NotificationDto>> {
        val offset = params.key ?: 0
        val loadSize = params.loadSize.toLong()

        return api.getNotification(
            limit = loadSize,
            offset = offset,
            explicit = 1
        )
            .subscribeOn(Schedulers.io())
            .map { toLoadResult(it, offset = offset, loadSize = loadSize) }
            .onErrorReturn { LoadResult.Error(it) }
    }

    private fun toLoadResult(
        data: List<NotificationDto>,
        offset: Long,
        loadSize: Long
    ): LoadResult<Long, NotificationDto> {
        return LoadResult.Page(
            data = data,
            prevKey = if (offset == 0L) null else offset - loadSize,
            nextKey = if (data.isEmpty()) null else offset + loadSize
        )
    }
}
