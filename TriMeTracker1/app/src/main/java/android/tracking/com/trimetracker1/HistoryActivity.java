package android.tracking.com.trimetracker1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.tracking.com.trimetracker1.adapter.HistoryAdapter;
import android.tracking.com.trimetracker1.data.LocationData;
import android.tracking.com.trimetracker1.data.LocationList;
import android.tracking.com.trimetracker1.data.Message;
import android.view.MenuItem;
import android.view.View;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.text.TextUtils.isEmpty;
import static android.tracking.com.trimetracker1.Utils.runOnBackgroundThread;
import static android.tracking.com.trimetracker1.Utils.runOnUIThread;

public class HistoryActivity extends AppCompatActivity {

    private List<LocationList> locationDataList = new ArrayList<>();
    private List<LocationList> locationDataListCopy = new ArrayList<>();
    private HistoryAdapter adapter = new HistoryAdapter();
    private RecyclerView recyclerView;
    private View viewEmpty;
    private int data_size = 0, counter = 0;

    private SessionDataListener eventListener = new SessionDataListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setRecyclerListener(mRecycleListener);
        viewEmpty = findViewById(R.id.textEmpty);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.history);
        }

        runOnBackgroundThread(() -> {
            String json = Session.getInstance().getPreferences(this).getJson("history");
            if (!isEmpty(json)) {
                //@formatter:off
                Type type = new TypeToken<List<LocationList>>() {}.getType();
                //@formatter:on
                locationDataList = Session.getInstance().gson().fromJson(json, type);
                runOnUIThread(() -> adapter.setLocationDataList(locationDataList));
            }
        });

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("messages");
        eventRef.orderByChild("receiverId").equalTo(currentUserId).addListenerForSingleValueEvent(eventListener);
    }

    /**
     * RecycleListener that completely clears the {@link com.google.android.gms.maps.GoogleMap}
     * attached to a row in the RecyclerView.
     * Sets the map type to {@link com.google.android.gms.maps.GoogleMap#MAP_TYPE_NONE} and clears
     * the map.
     */
    private RecyclerView.RecyclerListener mRecycleListener = holder -> {
        HistoryAdapter.ViewHolder mapHolder = (HistoryAdapter.ViewHolder) holder;
        if (mapHolder != null && mapHolder.googleMap != null) {
            // Clear the map and free up resources by changing the map type to none.
            // Also reset the map when it gets reattached to layout, so the previous map would
            // not be displayed.
            mapHolder.googleMap.clear();
            mapHolder.googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private class SessionDataListener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            runOnBackgroundThread(() -> {
                //@formatter:off
                GenericTypeIndicator<HashMap<String, Message>> objectsGTypeInd = new GenericTypeIndicator<HashMap<String, Message>>() {};
                //@formatter:on
                Map<String, Message> map = snapshot.getValue(objectsGTypeInd);
                if (map != null) {
                    runOnUIThread(HistoryActivity.this::hideEmpty);
                    ArrayList<Message> list = new ArrayList<>(map.values());
                    if (!list.isEmpty()) {
                        data_size = list.size();
                        for (Message msg : list) {
                            DatabaseReference locRef = FirebaseDatabase.getInstance().getReference().child("locations");
                            locRef.orderByChild("sessionId")
                                    .equalTo(msg.getSessionId())
                                    .addListenerForSingleValueEvent(new LocationDataListener(msg.getSenderName(), msg.getPlateNumber()));
                        }
                    }
                } else {
                    runOnUIThread(HistoryActivity.this::showEmpty);
                }
            });
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }

    private class LocationDataListener implements ValueEventListener {
        private String sender, plateNum;

        LocationDataListener(String sender, String plateNum) {
            this.sender = sender;
            this.plateNum = plateNum;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            runOnBackgroundThread(() -> {
                try {
                    //@formatter:off
                    GenericTypeIndicator<HashMap<String, LocationData>> objectsGTypeInd = new GenericTypeIndicator<HashMap<String, LocationData>>() {};
                    //@formatter:on
                    Map<String, LocationData> map = snapshot.getValue(objectsGTypeInd);
                    ArrayList<LocationData> list = new ArrayList<>(map.values());
                    if (!list.isEmpty()) {
                        locationDataListCopy.add(new LocationList(this.sender, plateNum, list));
                        counter++;
                        if (data_size == counter) {
                            locationDataList.clear();
                            locationDataList.addAll(locationDataListCopy);
                            locationDataListCopy.clear();
                            //@formatter:off
                            Type type = new TypeToken<List<LocationList>>() {}.getType();
                            //@formatter:on
                            String json = Session.getInstance().gson().toJson(locationDataList, type);
                            Session.getInstance().getPreferences(HistoryActivity.this).saveJson("history", json);
                            runOnUIThread(() -> adapter.setLocationDataList(locationDataList));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
            });
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }

    private void showEmpty() {
        viewEmpty.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideEmpty() {
        viewEmpty.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}
