package com.example.covid19notifier;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;


public class MessagingService extends FirebaseMessagingService {
    final String channelid = "channel1";
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        createnotification();
        showNotification(Objects.requireNonNull(remoteMessage.getNotification()).getBody());
    }

    private void showNotification(String message) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MessagingService.this, "channel1")
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setContentTitle("Covid'19 Update")
                .setContentText(message)
                .setColor(getResources().getColor(android.R.color.holo_red_dark))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MessagingService.this);
        notificationManager.notify(0, builder.build());
    }

    public void createnotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelid, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("This is a channel");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("Firebase",s);
    }
}