package android.tracking.com.trimetracker1.data;

import java.util.ArrayList;

public class LocationList {
    public String senderName, plateNum;
    public ArrayList<LocationData> data;

    public LocationList(String senderName, String plateNum, ArrayList<LocationData> data) {
        this.senderName = senderName;
        this.plateNum = plateNum;
        this.data = data;
    }
}
