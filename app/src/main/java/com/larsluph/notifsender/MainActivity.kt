package com.larsluph.notifsender

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun sendNotif(title: String, content: String) {
        val intent = Intent(this, SendNotifActivity::class.java).apply {
            putExtra("title", title)
            putExtra("content", content)
        }
        startActivity(intent)
    }
}