package com.maxime.go4lunch.viewmodel;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;

import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.maxime.go4lunch.R;
import com.maxime.go4lunch.api.UserHelper;
import com.maxime.go4lunch.model.Like;
import com.maxime.go4lunch.model.Restaurant;
import com.maxime.go4lunch.model.Workmate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class DrawerSharedViewModel extends ViewModel {

    public MutableLiveData<ArrayList<Restaurant>> liveRestaurant = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Workmate>> liveWorkmates = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Like>> liveLikes = new MutableLiveData<>();
    public MutableLiveData<Location> liveLocation = new MutableLiveData<>();

    public void getRestaurant(final Context context) {
        final PlacesClient placesClient = Places.createClient(context);
        List<Place.Field> arraylistField = new ArrayList<>();
        arraylistField.add(Place.Field.ID);
        arraylistField.add(Place.Field.TYPES);
        arraylistField.add(Place.Field.NAME);
        arraylistField.add(Place.Field.ADDRESS);
        //arraylistField.add(Place.Field.PHONE_NUMBER);
        //arraylistField.add(Place.Field.WEBSITE_URI);
        arraylistField.add(Place.Field.PHOTO_METADATAS);
        arraylistField.add(Place.Field.LAT_LNG);

        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(arraylistField);

        if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        FindCurrentPlaceResponse response = (FindCurrentPlaceResponse) task.getResult();
                        ArrayList<Restaurant> restaurants = new ArrayList<>();
                        for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                            if (Objects.requireNonNull(placeLikelihood.getPlace().getTypes()).contains(Place.Type.RESTAURANT)) {
                                Restaurant restaurant = new Restaurant(placeLikelihood);
                                getPlaceOpeningHours(placesClient, placeLikelihood.getPlace(), restaurant, context);
                                restaurants.add(restaurant);
                            }
                        }
                        liveRestaurant.setValue(restaurants);
                        if (liveWorkmates.getValue() != null) {
                            if (Objects.requireNonNull(liveWorkmates.getValue()).size() > 0) {
                                mapWorkmatesToRestaurant();
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

    public void updateLocation(Location location) {
        liveLocation.setValue(location);
    }

    public void getWorkmates() {
        UserHelper.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Workmate> workmates = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        Workmate workmate = document.toObject(Workmate.class);
                        assert workmate != null;
                        workmates.add(workmate);
                    }
                    liveWorkmates.setValue(workmates);
                    if (liveRestaurant.getValue() != null) {
                        if (Objects.requireNonNull(liveRestaurant.getValue()).size() > 0) {
                            mapWorkmatesToRestaurant();
                        }
                    }
                }
            }
        });
    }

    public void mapWorkmatesToRestaurant() {
        ArrayList<Restaurant> restaurants = liveRestaurant.getValue();
        for (Workmate workmate : Objects.requireNonNull(liveWorkmates.getValue())) {
            if (workmate.getRestaurant() != null) {
                assert restaurants != null;
                for (Restaurant restaurant : restaurants) {
                    if (restaurant.getName().equals(workmate.getRestaurant())) {
                        restaurant.getWorkmatesBeEating().add(workmate);
                    }
                }
            }
        }
        liveRestaurant.postValue(restaurants);
    }

    public void getAllLikes() {
        UserHelper.getLikesCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Like> likes = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        Like like = document.toObject(Like.class);
                        assert like != null;
                        likes.add(like);
                    }
                    liveLikes.setValue(likes);
                }
            }
        });
    }

    public void getLikesForRestaurant(Restaurant restaurant) {
        getAllLikes();
        ArrayList<Like> likes = liveLikes.getValue();
        for (Like like : likes) {
            if (like.getRestaurantId().equals(restaurant.getId())) {
                restaurant.getLikes().add(like);
            }
        }
    }

    public FetchPlaceRequest getFetchPlaceRequest(String id) {
        List<Place.Field> detailsArraylistField = new ArrayList<>();
        detailsArraylistField.add(Place.Field.ID);
        detailsArraylistField.add(Place.Field.OPENING_HOURS);
        detailsArraylistField.add(Place.Field.PHONE_NUMBER);
        detailsArraylistField.add(Place.Field.WEBSITE_URI);
        return FetchPlaceRequest.builder(id, detailsArraylistField)
                .build();
    }

    public void getPlaceOpeningHours(PlacesClient placesClient, Place place, final Restaurant restaurant, final Context context) {
        placesClient.fetchPlace(
                getFetchPlaceRequest(place.getId()))
                .addOnSuccessListener(
                        new OnSuccessListener<FetchPlaceResponse>() {
                            @Override
                            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                                fetchPlaceResponse.getPlace().getOpeningHours();
                                restaurant.setSchedule(getOpeningHoursStringForToday(fetchPlaceResponse.getPlace(), context));
                                restaurant.setPhoneNumber(fetchPlaceResponse.getPlace().getPhoneNumber());
                                restaurant.setWebSite(fetchPlaceResponse.getPlace().getWebsiteUri());
                            }
                        });
    }

    public String getOpeningHoursStringForToday(Place place, Context context) {
        if (place.getOpeningHours() != null) {
            List<Period> openingHoursList = place.getOpeningHours().getPeriods();
            for (Period period : openingHoursList) {
                if (Objects.requireNonNull(period.getOpen()).getDay().equals(getCurrentDayOfWeek())) {
                    return getReadableOpeningHours(period, context);
                }
            }
        } else {
            return context.getResources().getString(R.string.unknown_schedule);
        }
        return "";
    }

    public String getReadableOpeningHours(Period period, Context context) {
        String open = context.getResources().getString(R.string.open, "" + Objects.requireNonNull(period.getOpen()).getTime().getHours(), "" + getDisplayableMinutes(period));
        String close = context.getResources().getString(R.string.close, "" + Objects.requireNonNull(period.getClose()).getTime().getHours(), "" + getDisplayableMinutes(period));

        return open + " - " + close;
    }

    private String getDisplayableMinutes(Period period) {
        if (Objects.requireNonNull(period.getClose()).getTime().getMinutes() < 10) {
            return "0" + period.getClose().getTime().getMinutes();
        } else {
            return "" + period.getClose().getTime().getMinutes();
        }
    }

    private com.google.android.libraries.places.api.model.DayOfWeek getCurrentDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case 0:
                return com.google.android.libraries.places.api.model.DayOfWeek.MONDAY;
            case 1:
                return com.google.android.libraries.places.api.model.DayOfWeek.TUESDAY;
            case 2:
                return com.google.android.libraries.places.api.model.DayOfWeek.WEDNESDAY;
            case 3:
                return com.google.android.libraries.places.api.model.DayOfWeek.THURSDAY;
            case 4:
                return com.google.android.libraries.places.api.model.DayOfWeek.FRIDAY;
            case 5:
                return com.google.android.libraries.places.api.model.DayOfWeek.SATURDAY;
            case 6:
                return com.google.android.libraries.places.api.model.DayOfWeek.SUNDAY;
            default:
                return com.google.android.libraries.places.api.model.DayOfWeek.SUNDAY;
        }
    }
}