package android.tracking.com.trimetracker1;

import android.app.Application;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.UUID;

import static android.text.TextUtils.isEmpty;

public class Session extends Application {

    private static Session instance = null;

    private String uuid;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseMessaging.getInstance().subscribeToTopic("notifications");
    }

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public String sessionId() {
        if (isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
        }
        return uuid;
    }
}
