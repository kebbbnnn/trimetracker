package android.tracking.com.trimetracker1.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.tracking.com.trimetracker1.R;
import android.tracking.com.trimetracker1.Session;
import android.tracking.com.trimetracker1.Utils;
import android.tracking.com.trimetracker1.data.LocationData;
import android.tracking.com.trimetracker1.data.LocationList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private static final int BOUND_PADDING = 100;

    private List<LocationList> locationDataList;

    public class ViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        private MapView mapView;
        public GoogleMap googleMap;
        private TextView textSender, textPlateNum;
        private BitmapDescriptor dotIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            mapView = itemView.findViewById(R.id.map);
            mapView.onCreate(null);
            mapView.getMapAsync(this);
            textSender = itemView.findViewById(R.id.textLabel);
            textPlateNum = itemView.findViewById(R.id.textPlateNum);
            dotIcon = Utils.bitmapDescriptorFromVector(itemView.getContext(), R.drawable.ic_pin_dot);
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(Session.getInstance());
            GoogleMapOptions googleMapOptions = new GoogleMapOptions().liteMode(true);
            this.googleMap = googleMap;
            this.googleMap.getUiSettings().setAllGesturesEnabled(false);
            this.googleMap.setMapType(googleMapOptions.getMapType());
            setMapLocation();
        }

        private void bind(int position) {
            LocationList loc = locationDataList.get(position);
            mapView.setTag(loc);
            textSender.setText(loc.senderName.replace("\n", ""));
            textPlateNum.setText(loc.plateNum.replace("\n", ""));
            setMapLocation();
        }

        private void setMapLocation() {
            try {
                if (this.googleMap == null) return;

                LocationList data = (LocationList) mapView.getTag();
                if (data == null) return;

                List<LatLng> path = new ArrayList<>();
                for (LocationData loc : data.data) {
                    LatLng latLng = new LatLng(loc.lat, loc.lng);
                    //this.googleMap.addMarker(new MarkerOptions().position(latLng).icon(dotIcon));
                    this.googleMap.addMarker(new MarkerOptions().position(latLng));
                    path.add(latLng);
                }

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng latLng : path) {
                    builder.include(latLng);
                }

                final LatLngBounds bounds = builder.build();

                //BOUND_PADDING is an int to specify padding of bound.. try 100.
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, BOUND_PADDING);
                this.googleMap.animateCamera(cu);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setLocationDataList(List<LocationList> locationDataList) {
        this.locationDataList = locationDataList;
        notifyDataSetChanged();
    }

    public HistoryAdapter() {
        super();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        try {
            View root = inflater.inflate(R.layout.item_history, parent, false);
            return new ViewHolder(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder == null) {
            return;
        }
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return locationDataList != null ? locationDataList.size() : 0;
    }

}
