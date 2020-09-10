package com.example.roomie.house.chores.chore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.roomie.R;

public class SnoozerBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"notifyRoommie")
                .setSmallIcon(R.drawable.baseline_error_outline_black_18dp)
                .setContentText("just a reminder to do:  " +  intent.getStringExtra("title"))//TODO pass in intent the chore details
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200,builder.build());
    }
}
