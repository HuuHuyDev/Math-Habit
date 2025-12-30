package com.kidsapp.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kidsapp.R;
import com.kidsapp.data.local.SharedPref;
import com.kidsapp.ui.auth.LoginActivity;

/**
 * Service nhận Push Notification từ Firebase Cloud Messaging
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "mathhabit_notifications";
    private static final String CHANNEL_NAME = "MathHabit Notifications";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Notification payload (hiển thị tự động khi app ở background)
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Notification - Title: " + title + ", Body: " + body);
            
            // Hiển thị notification khi app ở foreground
            showNotification(title, body, remoteMessage.getData());
        }

        // Data payload (luôn nhận được)
        if (!remoteMessage.getData().isEmpty()) {
            Log.d(TAG, "Data payload: " + remoteMessage.getData());
            handleDataPayload(remoteMessage.getData());
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM token: " + token);
        
        // Lưu token vào SharedPref
        SharedPref sharedPref = new SharedPref(this);
        sharedPref.saveFcmToken(token);
        
        // Gửi token lên server nếu đã đăng nhập
        if (sharedPref.isLoggedIn()) {
            FcmTokenManager fcmTokenManager = new FcmTokenManager(this);
            fcmTokenManager.sendTokenToServer(token);
        }
    }

    /**
     * Xử lý data payload
     */
    private void handleDataPayload(java.util.Map<String, String> data) {
        String type = data.get("type");
        String referenceId = data.get("referenceId");
        
        if (type != null) {
            switch (type) {
                case "TASK_ASSIGNED":
                    // Có thể refresh danh sách task
                    Log.d(TAG, "New task assigned: " + referenceId);
                    break;
                case "TASK_COMPLETED":
                    Log.d(TAG, "Task completed: " + referenceId);
                    break;
                case "LEVEL_UP":
                    Log.d(TAG, "Level up!");
                    break;
            }
        }
    }

    /**
     * Hiển thị notification
     */
    private void showNotification(String title, String body, java.util.Map<String, String> data) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        // Thêm data vào intent để xử lý khi click
        if (data != null) {
            for (java.util.Map.Entry<String, String> entry : data.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = 
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Tạo notification channel cho Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Thông báo từ MathHabit");
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }

}
