package edu.northeastern.g15finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

public class MainScreenMapFragment extends Fragment {

    GoogleMap map =  null;
    TileOverlay heatmapOverlay = null;
    HeatmapTileProvider heatmapTileProvider = null;
    Boolean userLocationMapSync = true;
    Location lastSetLocation = null;

    FloatingActionButton reCenterButton = null;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(GoogleMap googleMap) {

            // Set map
            map = googleMap;
            map.setMyLocationEnabled(true);
            // Print the calling object
            System.out.println("CallingObject" + this);

            reCenterButton = getActivity().findViewById(R.id.location_recenter_button);

            // Get bundle from MainScreenFragment
            Bundle bundle = getArguments();
            System.out.println(bundle);

            UiSettings uiSettings = map.getUiSettings();
            uiSettings.setMyLocationButtonEnabled(false);
            uiSettings.setCompassEnabled(true);

            Location mapLocation = bundle.getParcelable("mapLocation");

            LatLng showLocation = new LatLng(mapLocation.getLatitude(), mapLocation.getLongitude());
//            LatLng sydney = new LatLng(-34, 151);
            System.out.println("Map is ready");
            googleMap.addMarker(new MarkerOptions().position(showLocation).title(bundle.getString("locationLabel")));
            // zoom level as close as possible
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(showLocation, 17));

            // Set the toggle button to invisible
            reCenterButton.setVisibility(reCenterButton.INVISIBLE);

            // Set the toggle button to visible when the camera is moved and the center of the view is not the same as the user's location
            googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {

                    Location userLocation = googleMap.getMyLocation();
                    if (userLocation != null) {
                        LatLng centerLatLng = googleMap.getCameraPosition().target;
                        Location centerLocation = new Location("");
                        centerLocation.setLatitude(centerLatLng.latitude);
                        centerLocation.setLongitude(centerLatLng.longitude);
                        if (userLocation.distanceTo(centerLocation) > 2) {
                            reCenterButton.setVisibility(reCenterButton.VISIBLE);
                        } else {
                            reCenterButton.setVisibility(reCenterButton.INVISIBLE);
                        }
                    }
                }
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        System.out.println("onCreateView");
        System.out.println(savedInstanceState);
        return inflater.inflate(R.layout.fragment_main_screen_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        System.out.println("View created");
        // Print saved instance state
        System.out.println(savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public void updateMapLocation(Location location) {

        System.out.println("Updating map location");

        if (!userLocationMapSync) {
            return;
        }

        if (heatmapOverlay != null) {
            heatmapOverlay.clearTileCache();
        }

        if (lastSetLocation == null || location.distanceTo(lastSetLocation) > 2) {
            if(map!=null){
                lastSetLocation = location;
                map.clear();
                LatLng showLocation = new LatLng(location.getLatitude(), location.getLongitude());
                map.addMarker(new MarkerOptions().position(showLocation).title("You are here"));
                if (heatmapOverlay != null) {
                    map.addTileOverlay(new com.google.android.gms.maps.model.TileOverlayOptions().tileProvider(heatmapTileProvider));
                    heatmapOverlay.clearTileCache();
                }
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(showLocation, 17));
            }
        }
    }

    public void updateMapLocation(LatLng latlng, String title){
//        Location l = new Location("");
//        l.setLatitude(latlng.latitude);
//        l.setLongitude(latlng.longitude);
//        updateMapLocation(l);
        if(map!=null){
            map.clear();
            map.addMarker(new MarkerOptions().position(latlng).title(title));

            if (heatmapOverlay != null) {
                map.addTileOverlay(new com.google.android.gms.maps.model.TileOverlayOptions().tileProvider(heatmapTileProvider));
                heatmapOverlay.clearTileCache();
            }

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
        }
    }

    public void reCenterMap() {
        if (map != null) {
            Location userLocation = map.getMyLocation();
            if (userLocation != null) {
                LatLng userLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 17));
            }
        }
    }

    public void stopLocationTracking() {
        userLocationMapSync = false;
    }

    public void startLocationTracking() {
        userLocationMapSync = true;
    }

    public void syncUserViewLastLocation() {
        if (lastSetLocation != null) {
            LatLng showLocation = new LatLng(lastSetLocation.getLatitude(), lastSetLocation.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(showLocation, 17));
        }
    }

    public void setHeatmap(HeatmapTileProvider heatmapTileProvider) {
        if (heatmapOverlay != null) {
            heatmapOverlay.remove();
        }
        System.out.println("Setting heatmap");
        this.heatmapTileProvider = heatmapTileProvider;
        heatmapOverlay = map.addTileOverlay(new com.google.android.gms.maps.model.TileOverlayOptions().tileProvider(heatmapTileProvider));
        heatmapOverlay.setVisible(true);
        heatmapOverlay.clearTileCache();
        System.out.println("IS VISIBLE : " + heatmapOverlay.isVisible());
        System.out.println("Heatmap set");
    }
}