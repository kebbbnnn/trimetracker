package android.tracking.com.trimetracker1;

import android.app.Application;
import android.content.Context;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.UUID;

import static android.text.TextUtils.isEmpty;

public class Session extends Application {

    private static Session instance = null;

    private Preferences preferences;
    private boolean onGoingSession = false;
    private String uuid;
    private Gson gson;

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

    public void endSession() {
        uuid = null;
        setOnGoingSession(false);
    }

    public Gson gson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public boolean isOnGoingSession() {
        return onGoingSession;
    }

    public void setOnGoingSession(boolean onGoingSession) {
        this.onGoingSession = onGoingSession;
    }

    public Preferences getPreferences(Context context) {
        if (preferences == null) {
            preferences = Preferences.getInstance(context);
        }
        return preferences;
    }


}
