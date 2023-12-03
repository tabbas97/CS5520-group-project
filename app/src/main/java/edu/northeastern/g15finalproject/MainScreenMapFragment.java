package edu.northeastern.g15finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainScreenMapFragment extends Fragment {

    GoogleMap map =  null;
    Boolean userLocationMapSync = true;
    Location lastSetLocation = null;

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
        @Override
        public void onMapReady(GoogleMap googleMap) {

            // Set map
            map = googleMap;
            // Print the calling object
            System.out.println("CallingObject" + this);

            // Get bundle from MainScreenFragment
            Bundle bundle = getArguments();
            System.out.println(bundle);

            Location mapLocation = bundle.getParcelable("mapLocation");

            LatLng showLocation = new LatLng(mapLocation.getLatitude(), mapLocation.getLongitude());
//            LatLng sydney = new LatLng(-34, 151);
            System.out.println("Map is ready");
            googleMap.addMarker(new MarkerOptions().position(showLocation).title(bundle.getString("locationLabel")));
            // zoom level as close as possible
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(showLocation, 17));

            googleMap.setOnCameraMoveListener(() -> {
                    System.out.println("Camera moved");
                    Button toggleButton = getActivity().findViewById(R.id.toggle_button);
                    // If the center of current view is not the same as the location of the user
                    // then show the toggle button
                    if (lastSetLocation != null && (googleMap.getCameraPosition().target.latitude != lastSetLocation.getLatitude() ||
                            googleMap.getCameraPosition().target.longitude != lastSetLocation.getLongitude())) {
                        toggleButton.setVisibility(toggleButton.VISIBLE);
                    } else {
                        toggleButton.setVisibility(toggleButton.INVISIBLE);
                    }
                }
            );
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
        if (!userLocationMapSync) {
            return;
        }

        if (lastSetLocation == null) {
            lastSetLocation = location;
            map.clear();
            LatLng showLocation = new LatLng(location.getLatitude(), location.getLongitude());
            map.addMarker(new MarkerOptions().position(showLocation).title("You are here"));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(showLocation, 17));
        } else if (location.distanceTo(lastSetLocation) > 2){
            map.clear();
            lastSetLocation = location;
            LatLng showLocation = new LatLng(location.getLatitude(), location.getLongitude());
            map.addMarker(new MarkerOptions().position(showLocation).title("You are here"));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(showLocation, 17));
        }
    }

    public void updateMapLocation(LatLng latlng){
        map.addMarker(new MarkerOptions().position(latlng).title("You are here"));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
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


}