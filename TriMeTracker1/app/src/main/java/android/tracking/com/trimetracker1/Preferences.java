package android.tracking.com.trimetracker1;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static Preferences instance = null;

    private SharedPreferences preferences;

    public static Preferences getInstance(Context context) {
        if (instance == null) {
            instance = new Preferences();
        }
        if (instance.preferences == null) {
            instance.preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        }
        return instance;
    }

    public String getJson(String key) {
        return preferences.getString(key, null);
    }

    public void saveJson(String key, String json) {
        preferences.edit().putString(key, json).apply();
    }
}
