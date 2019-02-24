package android.tracking.com.trimetracker1;

import android.os.Handler;
import android.os.Looper;

public class Utils {
    public static void runOnUIThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }
    public static void runOnBackgroundThread(Runnable runnable) {
        new Thread(runnable).start();
    }
}
