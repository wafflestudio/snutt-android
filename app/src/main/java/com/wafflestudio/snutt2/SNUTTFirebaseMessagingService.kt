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
        showNotification(title, body, path)
    }

    @OptIn(ExperimentalAnimationApi::class)
    private fun showNotification(title: String, body: String, route: String) {
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
            putExtra("url_scheme", route)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.snutt_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        nm.notify(System.currentTimeMillis().toInt(), notification)
    }
}
