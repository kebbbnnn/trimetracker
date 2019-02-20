package android.tracking.com.trimetracker1.data;

import com.google.firebase.database.Exclude;

public class LocationData {
    @Exclude
    public long id;
    public String session_id;
    public String userId;
    public double lat;
    public double lng;
    public long timestamp = 0L;

    //@formatter:off
    public LocationData() {
    }
    //@formatter:on

    public LocationData(String session_id, String userId, double lat, double lng, long timestamp) {
        this.session_id = session_id;
        this.userId = userId;
        this.lat = lat;
        this.lng = lng;
        this.timestamp = timestamp;
    }

    public LocationData(String session_id, String userId, double lat, double lng) {
        this.session_id = session_id;
        this.userId = userId;
        this.lat = lat;
        this.lng = lng;
        this.timestamp = this.timestamp == 0L ? System.currentTimeMillis() : this.timestamp;
    }

}
