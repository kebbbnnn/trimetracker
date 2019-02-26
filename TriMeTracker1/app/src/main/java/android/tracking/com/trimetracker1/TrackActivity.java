package android.tracking.com.trimetracker1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.tracking.com.trimetracker1.data.LocationData;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.*;

import static android.text.TextUtils.isEmpty;

public class TrackActivity extends FragmentActivity implements OnMapReadyCallback {

    private TextView textSenderName;

    private GoogleMap googleMap;
    private Marker marker;
    private DatabaseReference locRef;
    private ValueEventListener locationListener;
    private String senderId = null, senderName = null, plateNumber = null, sessionId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        textSenderName = findViewById(R.id.textLabel);
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                if (key.equals("senderId")) {
                    senderId = (String) getIntent().getExtras().get(key);
                }
                if (key.equals("senderName")) {
                    senderName = (String) getIntent().getExtras().get(key);
                }
                if (key.equals("plateNumber")) {
                    plateNumber = (String) getIntent().getExtras().get(key);
                }
                if (key.equals("sessionId")) {
                    sessionId = (String) getIntent().getExtras().get(key);
                }
            }
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;

        if (!isEmpty(senderName)) {
            textSenderName.setText(String.format("%s's live location", senderName));
        }

        if (!isEmpty(senderId)) {
            locRef = FirebaseDatabase.getInstance().getReference().child("locations");

            locationListener = locRef
                    .orderByChild("userId")
                    .equalTo(senderId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                LocationData data = snapshot.getValue(LocationData.class);

                                if (marker != null) {
                                    marker.remove();
                                }
                                LatLng latLng = new LatLng(data.lat, data.lng);
                                String details = "platenumber: " + plateNumber;
                                marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(details));
                                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("error", "Error: " + databaseError.getMessage());
                        }
                    });
        } else {
            //TODO: handle
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locRef != null) {
            locRef.removeEventListener(locationListener);
        }
    }
}
