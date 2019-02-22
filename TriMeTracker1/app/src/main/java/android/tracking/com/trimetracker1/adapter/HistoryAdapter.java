package android.tracking.com.trimetracker1.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.tracking.com.trimetracker1.R;
import android.tracking.com.trimetracker1.data.LocationList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.DirectionsWaypoint;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private static final int BOUND_PADDING = 100;

    private List<LocationList> locationDataList;

    class ViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        MapView mapView;
        MapboxMap mapboxMap;

        public ViewHolder(View itemView) {
            super(itemView);
            mapView = itemView.findViewById(R.id.mapView);
            mapView.onCreate(null);
        }

        @Override
        public void onMapReady(@NonNull MapboxMap mapboxMap) {
            this.mapboxMap = mapboxMap;
            this.mapboxMap.getUiSettings().setAllGesturesEnabled(false);
            this.mapboxMap.setStyle(Style.DARK);
            setMapLocation();
        }

        private void bind(int position) {
            LocationList loc = locationDataList.get(position);
            mapView.setTag(loc);
            setMapLocation();
        }

        private void setMapLocation() {
            try {
                if (mapboxMap == null) return;

                LocationList data = (LocationList) mapView.getTag();
                if (data == null) return;

                //              List<LatLng> path = new ArrayList<>();


                //Execute Directions API request
//                GeoApiContext context = new GeoApiContext.Builder()
//                        .apiKey("AIzaSyA41i7F9Hw7fGCvWnTNUKpBz2fwQOsnUlE")
//                        .build();
//
//                LocationData locOrigin = data.data.get(0);
//                String origin = locOrigin.lat + "," + locOrigin.lng;
//                LocationData locDestination = data.data.get(data.data.size() - 1);
//                String destination = locDestination.lat + "," + locDestination.lng;
//                Log.e("test", "origin: " + origin + ", destination: " + destination);

                DirectionsWaypoint origin = DirectionsWaypoint.builder().rawLocation(new double[]{-77.04341, 38.90962}).build();

                DirectionsWaypoint destination = DirectionsWaypoint.builder().rawLocation(new double[]{-77.0365, 38.8977}).build();

                MapboxDirections client = MapboxDirections.builder()
                        .accessToken("pk.eyJ1Ijoia2ViYmJubm4iLCJhIjoiY2pzZjBkaGl0MG8wZTQzczdvM2Fmbm1vNiJ9.C4e_NkvKOhjRvq-gDE1JdQ")
                        .origin(origin.location())
                        .destination(destination.location())
                        .profile(DirectionsCriteria.PROFILE_WALKING)
                        .build();

                client.enqueueCall(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
//                        List<LatLng> path = new ArrayList<>();
//                        try {
//                            List<DirectionsRoute> routes = response.body().routes();
//                            //Loop through legs and steps to get encoded polylines of each step
//                            if (routes != null && routes.size() > 0) {
//                                DirectionsRoute route = routes.get(0);
//                                if (route.legs() != null) {
//                                    for (int i = 0; i < route.legs().size(); i++) {
//                                        RouteLeg leg = route.legs().get(i);
//                                        if (leg.steps() != null) {
//                                            for (int j = 0; j < leg.steps().size(); j++) {
//                                                LegStep step = leg.steps().get(j);
//                                                if (step.intersections() != null && step.intersections().size() > 0) {
//                                                    for (int k = 0; k < step.intersections().size(); k++) {
//                                                        StepIntersection stepIntersection = step.intersections().get(k);
//                                                        Point point = stepIntersection.location();
//                                                        path.add(new LatLng(point.latitude(), point.longitude()));
//                                                    }
//                                                } else {
//                                                    Point point = step.maneuver().location();
//                                                    path.add(new LatLng(point.latitude(), point.longitude()));
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        } catch (Exception ex) {
//                            Log.e("test", ex.getLocalizedMessage());
//                        }
//
//                        //Draw the polyline
//                        if (path.size() > 0) {
//                            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
//                            mapboxMap.addPolyline(opts);
//                        }
//
//                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                        for (LatLng latLng : path) {
//                            builder.include(latLng);
//                        }
//
//                        final LatLngBounds bounds = builder.build();
//
//                        //BOUND_PADDING is an int to specify padding of bound.. try 100.
//                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, BOUND_PADDING);
//                        mapboxMap.animateCamera(cu);


                        if (response.body() == null) {
                            Log.e("test", "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e("test", "No routes found");
                            return;
                        }

                        // Retrieve the directions route from the API response
                        DirectionsRoute currentRoute = response.body().routes().get(0);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                    }

                });

//                DirectionsApiRequest req = DirectionsApi.getDirections(context, origin, destination);
//                try {
//                    DirectionsResult res = req.await();
//
//                    //Loop through legs and steps to get encoded polylines of each step
//                    if (res.routes != null && res.routes.length > 0) {
//                        DirectionsRoute route = res.routes[0];
//
//                        if (route.legs != null) {
//                            for (int i = 0; i < route.legs.length; i++) {
//                                DirectionsLeg leg = route.legs[i];
//                                if (leg.steps != null) {
//                                    for (int j = 0; j < leg.steps.length; j++) {
//                                        DirectionsStep step = leg.steps[j];
//                                        if (step.steps != null && step.steps.length > 0) {
//                                            for (int k = 0; k < step.steps.length; k++) {
//                                                DirectionsStep step1 = step.steps[k];
//                                                EncodedPolyline points1 = step1.polyline;
//                                                if (points1 != null) {
//                                                    //Decode polyline and add points to list of route coordinates
//                                                    List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
//                                                    for (com.google.maps.model.LatLng coord1 : coords1) {
//                                                        path.add(new LatLng(coord1.lat, coord1.lng));
//                                                    }
//                                                }
//                                            }
//                                        } else {
//                                            EncodedPolyline points = step.polyline;
//                                            if (points != null) {
//                                                //Decode polyline and add points to list of route coordinates
//                                                List<com.google.maps.model.LatLng> coords = points.decodePath();
//                                                for (com.google.maps.model.LatLng coord : coords) {
//                                                    path.add(new LatLng(coord.lat, coord.lng));
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                } catch (Exception ex) {
//                    Log.e("test", ex.getLocalizedMessage());
//                }
//
//                //Draw the polyline
//                if (path.size() > 0) {
//                    PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
//                    map.addPolyline(opts);
//                }
//
//                LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                for (LatLng latLng : path) {
//                    builder.include(latLng);
//                }
//
//                final LatLngBounds bounds = builder.build();
//
//                //BOUND_PADDING is an int to specify padding of bound.. try 100.
//                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, BOUND_PADDING);
//                map.animateCamera(cu);
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        Mapbox.getInstance(context, context.getString(R.string.mapbox_access_token));
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
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

    public static void runOnUIThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public static void runOnBackgroundThread(Runnable runnable) {
        new Thread(runnable).start();
    }
}
