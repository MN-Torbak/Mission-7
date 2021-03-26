package com.maxime.go4lunch.ui.mapview;

import android.Manifest;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.maxime.go4lunch.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {


    private GoogleMap mMap;

    public MapViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Places.initialize(getContext(), "AIzaSyC5PnYLjeSjD1CHBrujXoKqUt0yozB86bk");
        PlacesClient placesClient = Places.createClient(getActivity());
        test(placesClient);
        LocationManager locationManager = (LocationManager)
                getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (mMap == null) {
                    return;
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        } catch (SecurityException e) {
            Log.d("logmap", "requestlocationupdateerror");
        }
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //locationPermissionGranted = true;
                    //TODO: location ici
                    try {
                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    } catch (SecurityException e) {
                        Log.e("Exception: %s", e.getMessage());
                    }
                }
            }
        }
        //updateLocationUI();
    }

    void test(PlacesClient placesClient) {
        final List<Place.Field> placeFields = Collections.singletonList(Place.Field.NAME);
        final List<Place.Field> arraylistField = new ArrayList<>();
        arraylistField.add(Place.Field.TYPES);
        arraylistField.add(Place.Field.NAME);
        arraylistField.add(Place.Field.ADDRESS);
        //arraylistField.add(Place.Field.ADDRESS_COMPONENTS);
        //arraylistField.add(Place.Field.OPENING_HOURS);
        arraylistField.add(Place.Field.PHOTO_METADATAS);

        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(arraylistField);

        if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        FindCurrentPlaceResponse response = (FindCurrentPlaceResponse) task.getResult();
                        List<PlaceLikelihood> restaurants = new ArrayList<>();
                        for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                            if (placeLikelihood.getPlace().getTypes().contains(Place.Type.RESTAURANT)) {
                                restaurants.add(placeLikelihood);
                            }
                        }
                        Log.d("gg", "onComplete: ");
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            Log.e("bg", "Place not found: " + apiException.getStatusCode());
                        }
                    }
                }
            });
        }
    }

}







