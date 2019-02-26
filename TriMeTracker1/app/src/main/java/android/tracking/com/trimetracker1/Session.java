package android.tracking.com.trimetracker1;

import android.app.Application;
import android.content.Context;
import android.tracking.com.trimetracker1.data.Vehicle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private Vehicle vehicle;

    @Override
    public void onCreate() {
        super.onCreate();
        initPushNotification();
    }

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void initPushNotification() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : "empty";
        FirebaseMessaging.getInstance().subscribeToTopic("notifications-" + userId);
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

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
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
