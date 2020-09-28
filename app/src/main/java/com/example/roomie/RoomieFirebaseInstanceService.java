package com.example.roomie;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class RoomieFirebaseInstanceService extends FirebaseMessagingService {

    private final static String TAG = "ROOMIE_FIREBASE_INSTANCE_SERVICE";

    private final static String CHAT_NOTIFICATION_CHANNEL_ID = "ROOMIE_CHAT";


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, s);
        // TODO upload to server
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "Roomie Chat";
            String channelDesc = "Notification channel for the house chat.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHAT_NOTIFICATION_CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDesc);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHAT_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_home_24)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(new Random().nextInt(), builder.build());
    }
}
