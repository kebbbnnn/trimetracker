package android.tracking.com.trimetracker1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.tracking.com.trimetracker1.data.UserData;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class HomePageNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Button btnstarttrip;
    private Button btntrack;
    private CardView starttripcard, trackpassengercard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_nav);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        starttripcard = findViewById(R.id.start_trip);
        trackpassengercard = findViewById(R.id.track_passenger);

        View headerLayout = navigationView.getHeaderView(0);
        TextView textUser = headerLayout.findViewById(R.id.user);
        TextView textEmail = headerLayout.findViewById(R.id.email);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            textUser.setText(currentUser.getDisplayName());
            textEmail.setText(currentUser.getEmail());
        }

        starttripcard.setOnClickListener(v -> {
            Intent intent;
            if (Session.getInstance().isOnGoingSession()) {
                MapsActivity.setVehicle(Session.getInstance().getVehicle());
                intent = new Intent(HomePageNav.this, MapsActivity.class);
            } else {
                intent = new Intent(HomePageNav.this, StartTrip.class);
            }
            startActivity(intent);
        });

        trackpassengercard.setOnClickListener(v -> {
            Intent intent = new Intent(HomePageNav.this, SharedLocationActivity.class);
            startActivity(intent);
        });

        String userId = currentUser.getUid();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.orderByChild("id").equalTo(userId);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Context context = HomePageNav.this;
                UserData userData = snapshot.getChildren().iterator().next().getValue(UserData.class);
                if (userData == null || userData.contacts.isEmpty()) {
                    Session.getInstance().getPreferences(context).saveJson(userId + "-user", null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page_nav, menu);
        return true;
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            intent.putExtra(HistoryActivity.EXTRA_MY_TRIPS, false);
            startActivity(intent);
        } else if (id == R.id.nav_contacts) {
            startActivity(new Intent(this, AddContactsActivity.class));
        } else if (id == R.id.nav_mytrip) {
            Intent intent = new Intent(this, HistoryActivity.class);
            intent.putExtra(HistoryActivity.EXTRA_MY_TRIPS, true);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Session.getInstance().getPreferences(this).clearAll();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
