package com.larsluph.notifsender

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class DismissNotificationReceiver : BroadcastReceiver() {

    // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
    override fun onReceive(context: Context, intent: Intent) {
        val notifId = intent.extras!!.getInt("notifID")
        with(NotificationManagerCompat.from(context)) {
            cancel(notifId)
        }
    }
}