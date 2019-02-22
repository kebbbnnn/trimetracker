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
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.*;

public class HistoryActivity extends AppCompatActivity {

    private List<LocationList> locationDataList = new ArrayList<>();
    private HistoryAdapter adapter = new HistoryAdapter();
    private RecyclerView recyclerView;
    private int data_size = 0, counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("messages");
        eventRef.orderByChild("receiverId").equalTo(currentUserId).addListenerForSingleValueEvent(eventListener);
    }

    private ValueEventListener eventListener = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            //@formatter:off
            GenericTypeIndicator<HashMap<String, Message>> objectsGTypeInd = new GenericTypeIndicator<HashMap<String, Message>>() {};
            //@formatter:on
            Map<String, Message> map = snapshot.getValue(objectsGTypeInd);
            ArrayList<Message> list = new ArrayList<>(map.values());
            if (!list.isEmpty()) {
                data_size = list.size();
                for (Message msg : list) {
                    DatabaseReference locRef = FirebaseDatabase.getInstance().getReference().child("locations");
                    locRef.orderByChild("sessionId")
                            .equalTo(msg.getSessionId())
                            .addListenerForSingleValueEvent(locListener);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener locListener = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            try {
                //@formatter:off
                GenericTypeIndicator<HashMap<String, LocationData>> objectsGTypeInd = new GenericTypeIndicator<HashMap<String, LocationData>>() {};
                //@formatter:on
                Map<String, LocationData> map = snapshot.getValue(objectsGTypeInd);
                ArrayList<LocationData> list = new ArrayList<>(map.values());
                locationDataList.add(new LocationList(list));
                counter++;
                if (data_size == counter) {
                    adapter.setLocationDataList(locationDataList);
//                    for (int i = 0, size = locationDataList.size(); i < size; i++) {
//                        LocationList data = locationDataList.get(i);
//                        Log.e("test", "list [" + i + "]");
//                        for (LocationData loc : data.data) {
//                            Log.e("test", "loc sessionId: " + loc.sessionId);
//                        }
//                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
