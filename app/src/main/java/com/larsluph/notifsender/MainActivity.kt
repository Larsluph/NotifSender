package com.larsluph.notifsender

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val channelId = "Channel ID"
    private val channelName = "Notification Reminder"

    private var notifId: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.sendNotif).setOnClickListener {
            sendNotif(
                findViewById<TextView>(R.id.textTitle).text.toString(),
                findViewById<TextView>(R.id.textContent).text.toString(),
                findViewById<Switch>(R.id.switchPersistent).isChecked
            )
        }

        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog. Save the return value, an instance of
        // ActivityResultLauncher. You can use either a val, as shown in this snippet,
        // or a lateinit var in your onAttach() or onCreate() method.
        requestPermissionLauncher = registerForActivityResult(RequestPermission()) {}
    }

    private fun createNotificationChannel(): String {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance)
        // Register the channel with the system
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        return channelId
    }

    private fun sendNotif(title: String, content: String, isPersistent: Boolean = false) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) -> {
                // You can use the API that requires the permission.
            }

            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val builder = NotificationCompat.Builder(this, createNotificationChannel())
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content)
            )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(isPersistent)

        if (isPersistent) {
            val dismissIntent: Intent =
                Intent(this, DismissNotificationReceiver::class.java).apply {
                    putExtra(getString(R.string.extra_key_notifID), notifId)
                }

            val flags = FLAG_ONE_SHOT or FLAG_IMMUTABLE
            val dismissPendingIntent: PendingIntent =
                PendingIntent.getBroadcast(this, 0, dismissIntent, flags)

            builder.addAction(R.drawable.ic_launcher_foreground, "Dismiss", dismissPendingIntent)
        }

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(notifId++, builder.build())
        }
    }
}