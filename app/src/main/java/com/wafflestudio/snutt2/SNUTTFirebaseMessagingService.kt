package com.wafflestudio.snutt2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class SNUTTFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.data["title"]
            ?: remoteMessage.notification?.title
            ?: "알림"
        val body = remoteMessage.data["body"]
            ?: remoteMessage.notification?.body
            ?: ""
        val path = remoteMessage.data["url_scheme"]
            ?: remoteMessage.notification?.clickAction
            ?: ""
        showNotification(
            title = title,
            body = body,
            route = path,
            pushId = remoteMessage.data[PUSH_ID],
            pushType = remoteMessage.data[PUSH_TYPE],
        )
    }

    @OptIn(ExperimentalAnimationApi::class)
    private fun showNotification(title: String, body: String, route: String, pushId: String?, pushType: String?) {
        val channelId = "fcm_default_channel"
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.firebase_messaging_service_channel_name),
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
            }
            nm.createNotificationChannel(channel)
        }

        val intent = Intent(this, RootActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(RootActivity.URL_SCHEME, route)
            pushId?.let { putExtra(PUSH_ID, it) }
            pushType?.let { putExtra(PUSH_TYPE, it) }
        }
        // requestCode 가 같으면 이전 알림의 PendingIntent(extras)가 재사용되므로 알림마다 고유한 값을 쓴다.
        val notificationId = System.currentTimeMillis().toInt()
        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.snutt_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        nm.notify(notificationId, notification)
    }

    companion object {
        // 서버가 모든 푸시의 FCM data 에 넣는 키. 백그라운드 알림은 같은 키로 RootActivity intent extra 에 들어온다.
        const val PUSH_ID = "push_id"
        const val PUSH_TYPE = "push_type"
    }
}
