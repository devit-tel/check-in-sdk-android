package com.trueelogistics.example

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MainService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
//            val scanData : Map<String,String> = remoteMessage.scanData
            showNotification("=== Data == ",remoteMessage.data.toString())
        }
        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            showNotification("Heading Tesssst","this is a text")
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    private fun showNotification(title: String?, body: String?) {
        val intent = Intent()
        val pendingIntent = PendingIntent.getActivity(this@MainService, 0, intent,0)
        val notification = NotificationCompat.Builder(this@MainService,"1234")
            .setTicker("")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent).build()
        notification.flags = Notification.FLAG_AUTO_CANCEL
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)
    }
    override fun onMessageSent(p0: String?) {
    }
    override fun onNewToken(token: String?) {
    }
}