package com.zimoliv.buttonrush.database

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zimoliv.buttonrush.MainActivity2
import com.zimoliv.buttonrush.R

const val channelId = "notification_channel"
const val channelName = "com.zimoliv.buttonrush"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.notification != null) {
            remoteMessage.notification!!.title?.let { remoteMessage.notification!!.body?.let { it1 ->
                remoteMessage.notification!!.imageUrl?.let { it2 ->
                    generateNotofification(it,
                        it1, it2
                    )
                }
            } }
        }

    }

    private fun generateNotofification(title: String, message: String, uriImage: Uri) {
        val intent = Intent(this, MainActivity2::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        Glide.with(this)
            .asBitmap()
            .load(uriImage)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
                        .setSmallIcon(R.drawable.ic_baseline_ads_click_24)
                        .setAutoCancel(true)
                        .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                        .setOnlyAlertOnce(true)
                        .setContentIntent(pendingIntent)
                        .setLargeIcon(resource)
                        .setContentText(message)
                        .setContentTitle(title)

                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
                        notificationManager.createNotificationChannel(notificationChannel)
                    }

                    notificationManager.notify(0, builder.build())
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Code de gestion du chargement annulé, si nécessaire
                }
            })
    }
}