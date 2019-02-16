package android.tracking.com.trimetracker1;

import android.app.Application;

import com.google.firebase.messaging.FirebaseMessaging;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseMessaging.getInstance().subscribeToTopic("notifications");
    }
}
