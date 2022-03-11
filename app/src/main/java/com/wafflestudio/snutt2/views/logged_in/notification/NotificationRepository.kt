package com.wafflestudio.snutt2.views.logged_in.notification

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

// data 패키지에 들어가야 되는 것으로 보이지만 일단 다른 애들이랑 같이 모아둠
@Singleton
class NotificationRepository @Inject constructor(
    private val api: SNUTTRestApi
) {

    fun getNotification(limit : Long,
                        offset : Long,
                        explicit : Long
    ) : Pager<Long, NotificationDto> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                prefetchDistance = 5,
            ),
            pagingSourceFactory = {
                NotificationPagingSource(
                    api = api,
                    limit,
                    offset,
                    explicit)
            }
        )
    }
//    fun getNotification(
//        limit: Long,
//        offset: Long,
//        explicit: Long
//    ): Pager<Long, String> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = 20,
//                enablePlaceholders = true,
//                prefetchDistance = 5,
//            ),
//            pagingSourceFactory = {
//                NotificationPagingSource(
//                    api = api
//                )
//            }
//        )
//    }
//}

 @Singleton
 class NotificationPagingSource @Inject constructor(
    private val api: SNUTTRestApi,
    private val limit : Long,
    private val offset : Long,
    private val explicit : Long
 ) : RxPagingSource<Long, NotificationDto>() {
    override fun getRefreshKey(state: PagingState<Long, NotificationDto>): Long? {
        return state.anchorPosition?.let {
            return state.anchorPosition?.toLong()
        }
    }

    override fun loadSingle(params: LoadParams<Long>): Single<LoadResult<Long, NotificationDto>> {
        val offset = params.key ?: 0
        val loadSize = params.loadSize.toLong()

        return api.getNotification(
            limit= limit,
            offset= offset,
            explicit = explicit)
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
//@Singleton
//class NotificationPagingSource @Inject constructor(
//    private val api: SNUTTRestApi,
//) : RxPagingSource<Long, String>() {
//    override fun getRefreshKey(state: PagingState<Long, String>): Long? {
//        return state.anchorPosition?.let {
//            return state.anchorPosition?.toLong()
//        }
//    }
//
//    override fun loadSingle(params: LoadParams<Long>): Single<LoadResult<Long, String>> {
//        val offset = params.key ?: 0
//        val loadSize = params.loadSize.toLong()
//
//        return api.getTagList(2022, 1)
//            .subscribeOn(Schedulers.io())
//            .map { toLoadResult(it.instructor, offset, loadSize) }
//            .onErrorReturn { LoadResult.Error(it) }
//    }
//
//    private fun toLoadResult(
//        data: List<String>,
//        offset: Long,
//        loadSize: Long
//    ): LoadResult<Long, String> {
//        return LoadResult.Page(
//            data = data,
//            prevKey = if (offset == 0L) null else offset - loadSize,
//            nextKey = if (data.isEmpty()) null else offset + loadSize
//        )
//    }
}
