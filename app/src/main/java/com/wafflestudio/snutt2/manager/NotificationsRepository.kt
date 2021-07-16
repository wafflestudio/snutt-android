package com.wafflestudio.snutt2.manager

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava3.flowable
import com.wafflestudio.snutt2.data.NotificationsPagingSource
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.GetNotificationCountResults
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by makesource on 2017. 2. 27..
 */
@Singleton
class NotificationsRepository @Inject constructor(
    private val snuttRestApi: SNUTTRestApi,
    private val prefStorage: PrefStorage,
    private val pagingSource: NotificationsPagingSource
) {
    private var notifications: MutableList<NotificationDto?> = ArrayList()
    var fetched: Boolean = false

    interface OnNotificationReceivedListener {
        fun notifyNotificationReceived()
    }

    private val listeners: MutableList<OnNotificationReceivedListener> = ArrayList()
    fun addListener(current: OnNotificationReceivedListener) {
        for (listener in listeners) {
            if (listener == current) {
                Log.w(TAG, "listener reference is duplicated !!")
                return
            }
        }
        listeners.add(current)
    }

    fun removeListener(current: OnNotificationReceivedListener) {
        for (listener in listeners) {
            if (listener == current) {
                listeners.remove(listener)
                break
            }
        }
        Log.d(TAG, "listener count: " + listeners.size)
    }

    /* local method */
    fun reset() {
        notifications = ArrayList()
        fetched = false
    }

    fun getNotifications(): List<NotificationDto?> {
        return notifications
    }

    fun hasNotifications(): Boolean {
        return if (notifications == null || notifications!!.size > 0) true else false
    }

    fun loadData(offset: Int): Single<List<NotificationDto>> {
        return snuttRestApi.getNotification(
            limit = 20,
            offset = offset.toLong(),
            explicit = 1
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "get notification success!")
                for (notification in it) {
                    notifications!!.add(notification)
                }
            }
            .doOnError {
                Log.e(TAG, "get notification failed.")
            }
    }

    fun refreshNotification(): Single<List<NotificationDto>> {
        return snuttRestApi.getNotification(
            limit = 20,
            offset = 0,
            explicit = 1
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "get notification success!")
                notifications.clear()
                notifications.addAll(it)
            }
            .doOnError {
                Log.e(TAG, "get notification failed.")
            }
    }

    fun getNotificationCount(): Single<GetNotificationCountResults> {
        return snuttRestApi.getNotificationCount()
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "get notification count success!")
            }
            .doOnError {
                Log.e(TAG, "get notification count failed.")
            }
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

    companion object {
        private const val TAG = "NOTIFICATION_MANAGER"
    }
}
