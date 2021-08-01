package com.wafflestudio.snutt2.manager

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava3.flowable
import com.wafflestudio.snutt2.data.NotificationsPagingSource
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.GetNotificationCountResults
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsRepository @Inject constructor(
    private val snuttRestApi: SNUTTRestApi,
    private val pagingSource: NotificationsPagingSource
) {
    private val _refreshDataEvent = PublishSubject.create<Unit>()
    val refreshDataEvent: Observable<Unit> = _refreshDataEvent.hide()

    fun getNotificationCount(): Single<GetNotificationCountResults> {
        return snuttRestApi.getNotificationCount()
            .subscribeOn(Schedulers.io())
    }

    fun getPagedNotifications(): Flowable<PagingData<NotificationDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                prefetchDistance = 5,
            ),
            pagingSourceFactory = { pagingSource }
        ).flowable
    }

    fun triggerRefreshData() {
        _refreshDataEvent.onNext(Unit)
    }
}
