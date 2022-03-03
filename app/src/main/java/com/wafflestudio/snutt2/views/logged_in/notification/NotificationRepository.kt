package com.wafflestudio.snutt2.views.logged_in.notification

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.data.DataProvider
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.GetNotificationCountResults
import com.wafflestudio.snutt2.lib.network.dto.GetNotificationResults
import com.wafflestudio.snutt2.lib.toOptional
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.toObservable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

// data 패키지에 들어가야 되는 것으로 보이지만 일단 다른 애들이랑 같이 모아둠
@Singleton
class NotificationRepository @Inject constructor(
    private val storage : SNUTTStorage,
    private val api : SNUTTRestApi
){
    var notificationList = storage.notifications

    fun getNotification(limit : Long,
                        offset : Long,
                        explicit : Long
    ) : Single<GetNotificationResults> {
        return api.getNotification(
            limit,
            offset,
            explicit
        )
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .doOnSuccess{
                storage.notifications.update(it)
            }
    }

    fun getNotificationCount() : Single<GetNotificationCountResults> {
        return api.getNotificationCount()
    }
}
