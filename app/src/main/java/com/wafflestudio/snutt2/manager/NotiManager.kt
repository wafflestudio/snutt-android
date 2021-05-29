package com.wafflestudio.snutt2.manager

import android.util.Log
import com.wafflestudio.snutt2.network.dto.core.NotificationDto
import com.wafflestudio.snutt2.network.SNUTTRestApi
import com.wafflestudio.snutt2.network.dto.GetNotificationCountResults
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by makesource on 2017. 2. 27..
 */
@Singleton
class NotiManager @Inject constructor(
    private val snuttRestApi: SNUTTRestApi,
    private val prefStorage: PrefStorage
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
        val token = prefStorage.prefKeyXAccessToken
        return snuttRestApi.getNotification(
            token!!,
            limit = 20,
            offset = offset.toLong(),
            explicit = 1
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "get notification success!")
//                Refactoring FIXME: dirty progress
//                removeProgressBar()
                for (notification in it) {
                    notifications!!.add(notification)
                }
            }
            .doOnError {
                Log.e(TAG, "get notification failed.")
            }
    }

//    fun addProgressBar() {
//        // Refactoring FIXME: 더러운 코드
//        notifications!!.add(null)
//    }

//    fun removeProgressBar() {
//        notifications!!.removeAt(notifications!!.size - 1)
//    }

    fun refreshNotification(): Single<List<NotificationDto>> {
        val token = prefStorage.prefKeyXAccessToken
        return snuttRestApi.getNotification(
            token!!,
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
        val token = prefStorage.prefKeyXAccessToken
        return snuttRestApi.getNotificationCount(token!!)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "get notification count success!")
            }
            .doOnError {
                Log.e(TAG, "get notification count failed.")
            }
    }

    fun notifyNotificationReceived() {
        fetched = false
        for (listener in listeners) {
            listener.notifyNotificationReceived()
        }
    }

    companion object {
        private const val TAG = "NOTIFICATION_MANAGER"
    }
}
