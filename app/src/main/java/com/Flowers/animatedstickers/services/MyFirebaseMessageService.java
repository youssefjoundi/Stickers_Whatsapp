package com.Flowers.animatedstickers.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.Flowers.animatedstickers.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MyFirebaseMessageService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            Log.d("onMessageFirebase: ", remoteMessage.getData().toString());
            if (data.get("post_id") != null) {
                String _unique_id = data.get("unique_id");
                String title = data.get("title");
                String message = data.get("message");
                String big_image = data.get("big_image");
                String link = data.get("link");
                String _post_id = data.get("post_id");

                assert _unique_id != null;

                assert _post_id != null;
                long post_id = Long.parseLong(_post_id);

                createNotification(title, message, big_image, link, post_id);
            }
        }
    }

    private void createNotification(String title, String message, String image_url, String link, long post_id) {

        Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
        notificationIntent.setData(Uri.parse(link));

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = getApplicationContext().getString(R.string.app_name);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(getNotificationIcon(notificationBuilder))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            notificationBuilder.setPriority(Notification.PRIORITY_MAX);
        } else {
            notificationBuilder.setPriority(NotificationManager.IMPORTANCE_HIGH);
        }

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder.setSound(alarmSound);
        notificationBuilder.setVibrate(new long[]{100, 200, 300, 400});

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.shouldShowLights();
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }

        if (image_url != null && !image_url.isEmpty()) {
            Bitmap image = fetchBitmap(image_url);
            if (image != null) {
                notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image));
            }
        }

        //assert notificationManager != null;
        notificationManager.notify((int) post_id, notificationBuilder.build());
    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        return R.drawable.ic_notification;
    }

    private Bitmap fetchBitmap(String src) {
        try {
            if (src != null) {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setConnectTimeout(1200000);
                connection.setReadTimeout(1200000);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

