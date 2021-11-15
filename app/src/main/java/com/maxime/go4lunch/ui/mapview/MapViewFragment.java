package com.maxime.go4lunch.ui.mapview;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.maxime.go4lunch.R;
import com.maxime.go4lunch.model.Restaurant;
import com.maxime.go4lunch.viewmodel.SharedViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private SharedViewModel mSharedViewModel;
    private GoogleMap mMap;
    private LocationListener locationListener;
    private LocationManager locationManager;

    public MapViewFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        locationManager = (LocationManager)
                requireActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (mMap == null) {
                    return;
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
                mSharedViewModel.updateLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }

            @Override
            public void onProviderEnabled(@NonNull String provider) { }

            @Override
            public void onProviderDisabled(@NonNull String provider) { }
        };
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1000, 50, locationListener);
        } catch (SecurityException e) {
            Log.d("logmap", "requestlocationupdateerror");
        }
        requireActivity().findViewById(R.id.autocomplete_fragment).setVisibility(View.VISIBLE);
        requireActivity().findViewById(R.id.autocomplete_background).setVisibility(View.VISIBLE);
        requireActivity().findViewById(R.id.tri_spinner).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.button_tri).setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    private Restaurant getRestaurantFromMarker(Marker m, List<Restaurant> restaurants) {
        for (Restaurant restaurant : restaurants) {
            if (restaurant.getName().equals(m.getTitle())) {
                return restaurant;
            }
        }
        return null;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
        } catch (SecurityException e) {
            Log.d("logmap", "requestlocationupdateerror");
        }
        mSharedViewModel.liveRestaurant.observe(requireActivity(), new Observer<ArrayList<Restaurant>>() {
            @Override
            public void onChanged(final ArrayList<Restaurant> restaurants) {
                for (Restaurant currentRestaurant : restaurants) {
                    final LatLng position = (currentRestaurant.getLatlng());
                    int markerDrawable = R.drawable.orange_icon_lunch;
                    if (currentRestaurant.getWorkmatesEatingHere().size() > 0) {
                        markerDrawable = R.drawable.green_icon_lunch;
                    }
                    if (mMap != null) {
                        mMap.addMarker(new MarkerOptions()
                                .position(position)
                                .title(currentRestaurant.getName())
                                .snippet(currentRestaurant.getAddress())
                                .icon(BitmapDescriptorFactory
                                        .fromResource(markerDrawable)));
                    }

                }
                if (mMap != null) {
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker m) {
                            Bundle b = new Bundle();
                            b.putString("restaurant", Objects.requireNonNull(getRestaurantFromMarker(m, restaurants)).getId());
                            NavHostFragment.findNavController(MapViewFragment.this).navigate(R.id.action_navigation_map_view_to_restaurantDetailsFragment, b);
                            return false;
                        }
                    });
                }
            }
        });
    }

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) { mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                } catch (SecurityException e) {
                    Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
                }
            }
        }
    }

}







