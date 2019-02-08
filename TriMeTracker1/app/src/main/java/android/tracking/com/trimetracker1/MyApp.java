package android.tracking.com.trimetracker1;

import android.app.Application;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.util.HttpAuthorizer;

public class MyApp extends Application {

    public static Pusher pusher;

    @Override
    public void onCreate() {
        super.onCreate();
        PusherOptions options = new PusherOptions();
        options.setCluster("ap1");
        final HttpAuthorizer authorizer = new HttpAuthorizer(
                "http://www.leggetter.co.uk/pusher/pusher-examples/php/authentication/src/presence_auth.php");
        options.setAuthorizer(authorizer);
        pusher = new Pusher("5d4e20bf503366b00d14", options);
    }
}
