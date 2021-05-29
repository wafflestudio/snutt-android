package com.wafflestudio.snutt2.manager

import android.util.Log
import com.google.common.base.Preconditions
import com.wafflestudio.snutt2.SNUTTApplication
import com.wafflestudio.snutt2.model.Notification
import com.wafflestudio.snutt2.network.dto.GetNotificationCountResults
import com.wafflestudio.snutt2.network.dto.core.TempUtil
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

/**
 * Created by makesource on 2017. 2. 27..
 */
class NotiManager private constructor(app: SNUTTApplication) {
    private val app: SNUTTApplication
    private var notifications: MutableList<Notification>?
    var fetched: Boolean

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

    fun getNotifications(): List<Notification>? {
        return notifications
    }

    fun hasNotifications(): Boolean {
        return if (notifications == null || notifications!!.size > 0) true else false
    }

    fun loadData(offset: Int): Single<List<Notification>> {
        val token = PrefManager.instance!!.prefKeyXAccessToken
        return app.restService!!.getNotification(
            token!!,
            limit = 20,
            offset = offset.toLong(),
            explicit = 1
        )
            .map { it.map { TempUtil.toLegacyModel(it) } }
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "get notification success!")
                removeProgressBar()
                for (notification in it) {
                    notifications!!.add(notification)
                }
            }
            .doOnError {
                Log.e(TAG, "get notification failed.")
            }
    }

    fun addProgressBar() {
        // Refactoring FIXME:
//        notifications!!.add(null)
    }

    fun removeProgressBar() {
        notifications!!.removeAt(notifications!!.size - 1)
    }

    fun refreshNotification(): Single<List<Notification>> {
        val token = PrefManager.instance!!.prefKeyXAccessToken
        return app.restService!!.getNotification(
            token!!,
            limit = 20,
            offset = 0,
            explicit = 1
        )
            .map { it.map { TempUtil.toLegacyModel(it) } }
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "get notification success!")
                notifications!!.clear()
                for (notification in it) {
                    notifications!!.add(notification)
                }
            }
            .doOnError {
                Log.e(TAG, "get notification failed.")
            }
    }

    fun getNotificationCount(): Single<GetNotificationCountResults> {
        val token = PrefManager.instance!!.prefKeyXAccessToken
        return app.restService!!.getNotificationCount(token!!)
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
        var instance: NotiManager? = null
            private set

        fun getInstance(app: SNUTTApplication): NotiManager? {
            instance = NotiManager(app)
            return instance
        }
    }

    init {
        Preconditions.checkNotNull(app)
        this.app = app
        notifications = ArrayList()
        fetched = false
    }
}
