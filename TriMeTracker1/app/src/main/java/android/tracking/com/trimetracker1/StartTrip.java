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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.*;

import static android.text.TextUtils.isEmpty;

public class StartTrip extends AppCompatActivity {

    private static final String TAG = "StartTrip";

    private NfcAdapter mNfcAdapter;
    private TextView mTextView;
    private View nfcContainer, platenumberContainer;
    private EditText editPlatenumber;
    private Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trip);
        mTextView = findViewById(R.id.tv_nfc_detail);
        nfcContainer = findViewById(R.id.layout_nfc);
        platenumberContainer = findViewById(R.id.layoutPlatenumber);
        editPlatenumber = findViewById(R.id.editPlatenumber);
        buttonNext = findViewById(R.id.btnNext);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
        if (mNfcAdapter == null) {
            nfcContainer.setVisibility(View.GONE);
            platenumberContainer.setVisibility(View.VISIBLE);

            buttonNext.setOnClickListener(v -> {
                String plateNumber = editPlatenumber.getText().toString();
                Vehicle vehicle = new Vehicle();
                vehicle.platenumber = plateNumber;
                MapsActivity.setVehicle(vehicle);
                getWindow().getDecorView().post(() -> {
                    startActivity(new Intent(StartTrip.this, MapsActivity.class));
                    finish();
                });
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag iTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (iTag != null) {
                String nfc_code = TagReader.readTag(iTag, intent).substring(3);
                if (!isEmpty(nfc_code)) {
                    DatabaseReference vehicleRef = FirebaseDatabase.getInstance().getReference().child("vehicleinfo");
                    vehicleRef.child(nfc_code).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Vehicle data = snapshot.getValue(Vehicle.class);
                            mTextView.setText("platenumber: " + data.platenumber + "\n" + "owner: " + data.ownername);
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
                }
            } else {
                Toast.makeText(this, "unrecognized nfc tag", Toast.LENGTH_SHORT).show();
            }
        }
    }
}




