package android.tracking.com.trimetracker1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.tracking.com.trimetracker1.adapter.SharedLocationAdapter;
import android.tracking.com.trimetracker1.data.Message;
import android.tracking.com.trimetracker1.support.ItemClickSupport;
import android.view.MenuItem;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.*;

import static android.tracking.com.trimetracker1.Utils.runOnBackgroundThread;
import static android.tracking.com.trimetracker1.Utils.runOnUIThread;

public class SharedLocationActivity extends AppCompatActivity implements ItemClickSupport.OnItemClickListener {

    private RecyclerView recyclerView;
    private View viewEmpty;
    private List<Message> eventList = new ArrayList<>();
    private SharedLocationAdapter adapter = new SharedLocationAdapter();
    private SessionDataListener eventListener = new SessionDataListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_location);
        viewEmpty = findViewById(R.id.textEmpty);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("messages");
        eventRef.orderByChild("receiverId").equalTo(currentUserId).addValueEventListener(eventListener);
    }

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

    @Override
    public void onItemClick(RecyclerView parent, View view, int position, long id) {
        final Message event = adapter.getItem(position);
        Intent intent = new Intent(this, TrackActivity.class);
        intent.putExtra("event", event.getEvent());
        intent.putExtra("receiverId", event.getReceiverId());
        intent.putExtra("senderId", event.getSenderId());
        intent.putExtra("senderName", event.getSenderName());
        intent.putExtra("plateNumber", event.getPlateNumber());
        intent.putExtra("sessionId", event.getSessionId());
        startActivity(intent);
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
                    runOnUIThread(SharedLocationActivity.this::hideEmpty);
                    List<Message> temp = new ArrayList<>(map.values());
                    eventList.clear();
                    for (Message message : temp) {
                        if (message.isLive()) {
                            eventList.add(message);
                        }
                    }
                    Collections.sort(eventList, new SortMessageByDate());
                    runOnUIThread(() -> adapter.setEventList(eventList));
                } else {
                    runOnUIThread(SharedLocationActivity.this::showEmpty);
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

    class SortMessageByDate implements Comparator<Message> {

        @Override
        public int compare(Message o1, Message o2) {
            return (int) (o2.getCreatedAt() - o1.getCreatedAt());
        }
    }
}
