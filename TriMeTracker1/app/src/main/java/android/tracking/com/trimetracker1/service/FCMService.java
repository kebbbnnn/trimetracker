package android.tracking.com.trimetracker1.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.tracking.com.trimetracker1.R;
import android.tracking.com.trimetracker1.TrackActivity;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.text.TextUtils.isEmpty;

public class FCMService extends FirebaseMessagingService {

    private static final String TAG = FCMService.class.getName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        /* There are two types of messages data messages and notification messages. Data messages are handled here in onMessageReceived whether the app is in the foreground or background. Data messages are the type traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app is in the foreground. When the app is in the background an automatically generated notification is displayed. */

        String notificationTitle = null, notificationBody = null;
        String event = null, receiverId = null, senderId = null, senderName = null, plateNumber = null;

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData().get("receiverId"));
            event = remoteMessage.getData().get("event");
            receiverId = remoteMessage.getData().get("receiverId");
            senderId = remoteMessage.getData().get("senderId");
            senderName = remoteMessage.getData().get("senderName");
            plateNumber = remoteMessage.getData().get("plateNumber");
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && !isEmpty(receiverId) && currentUser.getUid().equals(receiverId)) {
            // Also if you intend on generating your own notifications as a result of a received FCM
            // message, here is where that should be initiated. See sendNotification method below.
            sendNotification(notificationTitle, notificationBody, event, receiverId, senderId, senderName, plateNumber);
        }
    }

    /**
     * //     * Create and show a simple notification containing the received FCM message.
     * //
     */
    private void sendNotification(String notificationTitle, String notificationBody, String event, String receiverId, String senderId, String senderName, String plateNumber) {
        Intent intent = new Intent(this, TrackActivity.class);
        intent.putExtra("event", event);
        intent.putExtra("receiverId", receiverId);
        intent.putExtra("senderId", senderId);
        intent.putExtra("senderName", senderName);
        intent.putExtra("plateNumber", plateNumber);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = getNotificationManager();

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    /**
     * Gets notification manager
     *
     * @return
     */
    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }
}