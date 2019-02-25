package android.tracking.com.trimetracker1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.tracking.com.trimetracker1.adapter.AddContactsAdapter;
import android.tracking.com.trimetracker1.data.UserData;
import android.tracking.com.trimetracker1.support.ItemClickSupport;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class AddContactsActivity extends AppCompatActivity implements ItemClickSupport.OnItemClickListener {

    private RecyclerView recyclerView;
    private AddContactsAdapter adapter;
    private List<UserData> chosenUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.add_contacts);
        }

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddContactsAdapter();
        recyclerView.setAdapter(adapter);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(RecyclerView parent, View view, int position, long id) {
        AddContactsAdapter.ViewHolder holder = (AddContactsAdapter.ViewHolder) parent.findContainingViewHolder(view);
        UserData user = adapter.getUser(position);
        if (!holder.added) {
            holder.added = true;
            holder.check.setVisibility(View.VISIBLE);
            chosenUsers.add(user);
        } else {
            holder.added = false;
            holder.check.setVisibility(View.GONE);
            chosenUsers.remove(user);
        }
        for (UserData u : chosenUsers) {
            Log.e("test", "user name: " + u.name);
        }
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_contact_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.btnAdd) {
            // do something here
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
            usersRef.orderByChild("id").equalTo(currentUser.getUid());
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.e("test", "data: " + dataSnapshot.toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
