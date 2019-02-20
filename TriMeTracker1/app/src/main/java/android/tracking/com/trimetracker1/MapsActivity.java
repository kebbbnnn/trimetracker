package android.tracking.com.trimetracker1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.tracking.com.trimetracker1.adapter.ContactsListAdapter;
import android.tracking.com.trimetracker1.data.LocationData;
import android.tracking.com.trimetracker1.data.Message;
import android.tracking.com.trimetracker1.data.UserData;
import android.tracking.com.trimetracker1.data.Vehicle;
import android.tracking.com.trimetracker1.support.ItemClickSupport;
import android.util.Log;
import android.view.View;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import static android.text.TextUtils.isEmpty;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ItemClickSupport.OnItemClickListener {
    private static final long LOCATION_UPDATE_INTERVAL = 30000L;
    public static Vehicle vehicle;
    private SlidingUpPanelLayout slidingLayout;
    private ContactsListAdapter adapter;
    private RecyclerView list;
    private GoogleMap mMap;
    private Marker marker;
    private long lastLocUpdate = 0;
    private FirebaseUser currentUser;
    private DatabaseReference msgRef;
    private String sessionId;

    public static void setVehicle(Vehicle _vehicle) {
        vehicle = _vehicle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        slidingLayout = findViewById(R.id.sliding_layout);
        list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactsListAdapter();
        list.setAdapter(adapter);
        ItemClickSupport.addTo(list).setOnItemClickListener(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        sessionId = Session.getInstance().sessionId();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        msgRef = FirebaseDatabase.getInstance().getReference("messages");
    }

    @Override
    public void onItemClick(RecyclerView parent, View view, int position, long id) {
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        UserData user = adapter.users.get(position);
        Message message = new Message(sessionId, "event-location-share", currentUser.getDisplayName(), currentUser.getUid(), user.id, vehicle.platenumber);
        msgRef.push().setValue(message);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the currentUser will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the currentUser has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            updateLocation();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                updateLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void updateLocation() {
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(location -> {
            if (marker != null) {
                marker.remove();
            }
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            String details = "platenumer: " + vehicle.platenumber;
            if (!isEmpty(vehicle.ownername)) {
                details += ", owner: " + vehicle.ownername;
            }
            marker = mMap.addMarker(new MarkerOptions().position(latLng).title(details));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            if (SystemClock.elapsedRealtime() - lastLocUpdate < LOCATION_UPDATE_INTERVAL) {
                return;
            }
            lastLocUpdate = SystemClock.elapsedRealtime();

            saveLocationToDb(location.getLatitude(), location.getLongitude());
        });
    }

    private void saveLocationToDb(double lat, double lng) {
        DatabaseReference locRef = FirebaseDatabase.getInstance().getReference().child("locations").push();
        LocationData locData = new LocationData(sessionId, currentUser.getUid(), lat, lng);
        locRef.setValue(locData, (error, databaseReference) -> {
            if (error != null) {
                Log.e("test", "Error saving location to database, Error: " + error.getMessage() + ", Details: " + error.getDetails() + ", Code: " + error.getCode());
            } else {
                Log.e("test", "Location successfully saved!");
            }
        });
    }
}
