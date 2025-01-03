package com.example.deliveryapp

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FirebaseMessagingService: FirebaseMessagingService() {
    private val tag: String = "DEBUG"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(tag, "From: ${remoteMessage.from}")

        remoteMessage.notification?.let {
            Log.d(tag, "Notification - title: ${it.title}")
            Log.d(tag, "Notification - body : ${it.body}")
        } ?: Log.d(tag, "No Notification")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(tag, "Data: ${remoteMessage.data}")
        } else {
            Log.d(tag, "No Data")
        }

    }

    override fun onNewToken(token: String) {
        Log.d(tag, "Refreshed token: $token")
    }
}