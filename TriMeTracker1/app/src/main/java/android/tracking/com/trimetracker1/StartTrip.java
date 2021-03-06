package android.tracking.com.trimetracker1;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.tracking.com.trimetracker1.data.Vehicle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.*;

import static android.text.TextUtils.isEmpty;

public class StartTrip extends AppCompatActivity {

    private static final String TAG = "StartTrip";

    private NfcAdapter mNfcAdapter;
    private TextView mTextView;
    private View nfcContainer, platenumberContainer;
    private EditText editPlatenumber;
    private Button buttonNext;
    private TextView textLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trip);
        mTextView = findViewById(R.id.tv_nfc_detail);
        nfcContainer = findViewById(R.id.layout_nfc);
        platenumberContainer = findViewById(R.id.layoutPlatenumber);
        editPlatenumber = findViewById(R.id.editPlatenumber);
        buttonNext = findViewById(R.id.btnNext);
        textLabel = findViewById(R.id.textLabel);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
        if (mNfcAdapter == null) {
            openPlatenumberContainer("This device is not supported with NFC. Please type platenumber.");
            buttonNext.setOnClickListener(v -> {
                try {
                    String plateNumber = editPlatenumber.getText().toString();
                    Vehicle vehicle = new Vehicle();
                    vehicle.platenumber = plateNumber;
                    Session.getInstance().setVehicle(vehicle);
                    MapsActivity.setVehicle(vehicle);
                    getWindow().getDecorView().post(() -> {
                        startActivity(new Intent(StartTrip.this, MapsActivity.class));
                        finish();
                    });
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            });
            return;
        }
        nfcContainer.setVisibility(View.VISIBLE);
        platenumberContainer.setVisibility(View.GONE);
        if (!mNfcAdapter.isEnabled()) {
            startActivity(new Intent("android.settings.NFC_SETTINGS"));
            Toast.makeText(this, "nfc device not turned on", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNfcAdapter != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                    getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            IntentFilter[] intentFilters = new IntentFilter[]{};
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        }
    }

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag iTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (iTag != null) {
                String nfc_code = TagReader.readTag(iTag, intent);
                if (!isEmpty(nfc_code)) {
                    nfc_code = nfc_code.substring(3);
                    DatabaseReference vehicleRef = FirebaseDatabase.getInstance().getReference().child("vehicleinfo");
                    vehicleRef.child(nfc_code).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Vehicle data = snapshot.getValue(Vehicle.class);
                            mTextView.setText("platenumber: " + data.platenumber + "\n" + "owner: " + data.ownername);
                            Session.getInstance().setVehicle(data);
                            MapsActivity.setVehicle(data);
                            getWindow().getDecorView().postDelayed(() -> {
                                startActivity(new Intent(StartTrip.this, MapsActivity.class));
                                finish();
                            }, 3000);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(this, "unrecognized nfc tag", Toast.LENGTH_SHORT).show();
                    openPlatenumberContainer("Unrecognized NFC tag. Please type platenumber.");
                }
            } else {
                Toast.makeText(this, "unrecognized nfc tag", Toast.LENGTH_SHORT).show();
                openPlatenumberContainer("Unrecognized NFC tag. Please type platenumber.");
            }
        }
    }

    private void openPlatenumberContainer(String label) {
        textLabel.setText(label);
        nfcContainer.setVisibility(View.GONE);
        platenumberContainer.setVisibility(View.VISIBLE);
    }
}




