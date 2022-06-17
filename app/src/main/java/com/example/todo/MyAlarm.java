package com.example.todo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class MyAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder  builder = new NotificationCompat.Builder(context, "notify")
                .setSmallIcon(R.drawable.done_black)
                .setContentText("Remainder from ToDo")
                .setContentTitle(intent.getExtras().getString("title"))
                .setPriority(NotificationCompat.PRIORITY_HIGH);



        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Log.i("INFO", "NOTIFICATION OCCURS");

        notificationManager.notify(intent.getExtras().getInt("Id"), builder.build());
    }
}
