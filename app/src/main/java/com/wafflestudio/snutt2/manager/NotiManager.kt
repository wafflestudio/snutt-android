package com.wafflestudio.snutt2.manager

import android.util.Log
import com.google.common.base.Preconditions
import com.wafflestudio.snutt2.SNUTTApplication
import com.wafflestudio.snutt2.model.Notification
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.util.*

/**
 * Created by makesource on 2017. 2. 27..
 */
class NotiManager private constructor(app: SNUTTApplication) {
    private val app: SNUTTApplication
    private var notifications: MutableList<Notification?>?
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

    fun getNotifications(): List<Notification?>? {
        return notifications
    }

    fun hasNotifications(): Boolean {
        return if (notifications == null || notifications!!.size > 0) true else false
    }

    fun loadData(offset: Int, callback: Callback<Any>) {
        val token = PrefManager.instance!!.prefKeyXAccessToken
        val query: MutableMap<Any?, Any?> = HashMap<Any?, Any?>()
        query["limit"] = 20
        query["offset"] = offset
        query["explicit"] = 1 // for unread count update
        app.restService.getNotification(token, query, object : Callback<List<Notification?>> {
            override fun success(notificationList: List<Notification?>, response: Response) {
                Log.d(TAG, "get notification success!")
                removeProgressBar()
                for (notification in notificationList) {
                    notifications!!.add(notification)
                }
                callback.success(notificationList, response)
            }

            override fun failure(error: RetrofitError) {
                Log.e(TAG, "get notification failed.")
                callback.failure(error)
            }
        })
    }

    fun addProgressBar() {
        notifications!!.add(null)
    }

    fun removeProgressBar() {
        notifications!!.removeAt(notifications!!.size - 1)
    }

    fun refreshNotification(callback: Callback<Any>) {
        val token = PrefManager.instance!!.prefKeyXAccessToken
        val query: MutableMap<Any?, Any?> = HashMap<Any?, Any?>()
        query["limit"] = 20
        query["offset"] = 0
        query["explicit"] = 1
        app.restService.getNotification(token, query, object : Callback<List<Notification?>> {
            override fun success(notificationList: List<Notification?>, response: Response) {
                Log.d(TAG, "get notification success!")
                notifications!!.clear()
                for (notification in notificationList) {
                    notifications!!.add(notification)
                }
                callback.success(notificationList, response)
            }

            override fun failure(error: RetrofitError) {
                Log.e(TAG, "get notification failed.")
                callback.failure(error)
            }
        })
    }

    fun getNotificationCount(callback: Callback<Any>) {
        val token = PrefManager.instance!!.prefKeyXAccessToken
        app.restService.getNotificationCount(token, object : Callback<Map<String?, Int?>?> {
            override fun success(map: Map<String?, Int?>?, response: Response) {
                Log.d(TAG, "get notification count success!")
                callback.success(map, response)
            }

            override fun failure(error: RetrofitError) {
                Log.e(TAG, "get notification count failed.")
                callback.failure(error)
            }
        })
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