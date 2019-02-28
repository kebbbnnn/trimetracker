package android.tracking.com.trimetracker1;

import android.app.ProgressDialog;
import android.content.Context;
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

    private final FirebaseUser CURRENT_USER = FirebaseAuth.getInstance().getCurrentUser();
    private RecyclerView recyclerView;
    private AddContactsAdapter adapter;
    private List<UserData> chosenUsers = new ArrayList<>();
    private ProgressDialog dialog;
    private DatabaseReference usersRef;

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
        adapter = new AddContactsAdapter(this);
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
            dialog = ProgressDialog.show(this, "", "Adding contacts. Please wait...", true);
            final String userId = CURRENT_USER.getUid();
            usersRef = FirebaseDatabase.getInstance().getReference().child("users");
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        UserData found = child.getValue(UserData.class);
                        if (found != null && found.id.equals(userId)) {
                            String key = child.getKey();
                            usersRef.child(key)
                                    .child("contacts")
                                    .setValue(chosenUsers)
                                    .addOnSuccessListener(aVoid -> finishingUp());
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    dialog.dismiss();
                }
            });
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void finishingUp() {
        final String userId = CURRENT_USER.getUid();
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    UserData found = child.getValue(UserData.class);
                    if (found != null && found.id.equals(userId)) {
                        final Context context = AddContactsActivity.this;
                        String json = Session.getInstance().gson().toJson(found);
                        Log.e("test", "json: " + json);
                        Session.getInstance().getPreferences(context).saveJson(found.id + "-user", json);

                        dialog.dismiss();
                        finish();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
            }
        });
    }
}
