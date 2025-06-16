package com.ponto.controledeponto

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MyFirebaseMsgService", "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("MyFirebaseMsgService", "From: ${remoteMessage.from}")

        // Verifica se a mensagem contém uma carga útil de dados
        remoteMessage.data.isNotEmpty().let {
            Log.d("MyFirebaseMsgService", "Message data payload: ${remoteMessage.data}")
        }

        // Verifica se a mensagem contém uma carga útil de notificação
        remoteMessage.notification?.let {
            Log.d("MyFirebaseMsgService", "Message Notification Body: ${it.body}")
            // Exibir a notificação
        }
    }
}
