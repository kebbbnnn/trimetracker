package android.tracking.com.trimetracker1;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class StartTrip extends AppCompatActivity {

        private static final String TAG = "StartTrip";

        private NfcAdapter mNfcAdapter;
        private TextView mTextView;
        private ListView searchvehicle;
        ArrayAdapter<String> adapter; //for search

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_start_trip);
            mTextView = findViewById(R.id.tv_nfc_detail);

            //search for data info of vehicle
            searchvehicle = (ListView) findViewById(R.id.vehicleid);

            ArrayList<String> arrayVehicleID = new ArrayList<>();
            arrayVehicleID.addAll(Arrays.asList(getResources().getStringArray(R.array.vehicleinfo)));

            adapter = new ArrayAdapter<String>(
                    StartTrip.this,
                    android.R.layout.simple_list_item_1,
                    arrayVehicleID
            );
            searchvehicle.setAdapter(adapter);



            mNfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
            if (mNfcAdapter == null) {
                Toast.makeText(this, "This device is not supported with nfc", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            if (!mNfcAdapter.isEnabled()) {
                startActivity(new Intent("android.settings.NFC_SETTINGS"));
                Toast.makeText(this, "nfc device not turned on", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPause() {
            super.onPause();
            mNfcAdapter.disableForegroundDispatch(this);
        }

        @Override
        protected void onResume() {
            super.onResume();
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                    getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            IntentFilter[] intentFilters = new IntentFilter[]{};
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        }

        @Override
        protected void onNewIntent(Intent intent) {
            super.onNewIntent(intent);
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
                    || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
                Tag iTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                mTextView.setText(TagReader.readTag(iTag, intent));
            }
        }
    }




