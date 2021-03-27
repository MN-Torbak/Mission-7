package com.maxime.go4lunch.viewmodel;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.maxime.go4lunch.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class DrawerSharedViewModel extends ViewModel {

    public ArrayList<Restaurant> restaurants;//TODO turn into liveData for a fancier implementation

    public void getRestaurant(PlacesClient placesClient, Context context) {
        List<Place.Field> arraylistField = new ArrayList<>();
        arraylistField.add(Place.Field.TYPES);
        arraylistField.add(Place.Field.NAME);
        arraylistField.add(Place.Field.ADDRESS);
        arraylistField.add(Place.Field.PHOTO_METADATAS);

        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(arraylistField);

        if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        FindCurrentPlaceResponse response = (FindCurrentPlaceResponse) task.getResult();
                        restaurants = new ArrayList<>();
                        for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                            if (placeLikelihood.getPlace().getTypes().contains(Place.Type.RESTAURANT)) {
                                restaurants.add(new Restaurant(placeLikelihood));
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
