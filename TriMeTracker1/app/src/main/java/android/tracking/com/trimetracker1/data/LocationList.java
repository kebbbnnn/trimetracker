package android.tracking.com.trimetracker1.data;

import java.util.ArrayList;

public class LocationList {
    public String senderName;
    public ArrayList<LocationData> data;

    public LocationList(String senderName, ArrayList<LocationData> data) {
        this.senderName = senderName;
        this.data = data;
    }
}
