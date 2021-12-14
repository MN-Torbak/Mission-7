package com.maxime.go4lunch.viewmodel;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

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
import com.google.firebase.auth.FirebaseUser;
import com.maxime.go4lunch.R;
import com.maxime.go4lunch.api.UserManager;
import com.maxime.go4lunch.api.UserRepository;
import com.maxime.go4lunch.model.Like;
import com.maxime.go4lunch.model.Restaurant;
import com.maxime.go4lunch.model.Workmate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SharedViewModel extends ViewModel {

    UserManager mUserManager;
    public SharedViewModel() {
        mUserManager = new UserManager();
    }

    public MutableLiveData<ArrayList<Restaurant>> liveRestaurant = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Workmate>> liveWorkmates = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Like>> liveLikes = new MutableLiveData<>();
    public MutableLiveData<Location> liveLocation = new MutableLiveData<>();
    public MutableLiveData<Restaurant> liveMyRestaurant = new MutableLiveData<>();

    public void getUsersCollection(UserRepository.WorkmatesListener listener) {
        mUserManager.getUsersCollection(listener);
    }

    public void getLikesCollection(UserRepository.LikesListener listener) {
        mUserManager.getLikesCollection(listener);
    }

    public Task<Void> createUser(String id, String avatar, String name) {
        return mUserManager.createUser(id, avatar, name);
    }

    public void createLike(String id, String workmateId, String restaurantId) {
        mUserManager.createLike(id, workmateId, restaurantId);
    }

    public void getUser(String id, UserRepository.OnUserSuccessListener listener) {
        mUserManager.getUser(id, listener);
    }

    public FirebaseUser getCurrentUser() {
        return mUserManager.getCurrentUser();
    }

    public Task<Void> updateUserName(String id, String name) {
        return mUserManager.updateUserName(id, name);
    }

    public Task<Void> updateUserAvatar(String id, String avatar) {
        return mUserManager.updateUserAvatar(id, avatar);
    }

    public void updateUserRestaurant(String id, String restaurant) {
        mUserManager.updateUserRestaurant(id, restaurant);
    }

    public void updateUserRestaurantAddress(String id, String restaurant_address) {
        mUserManager.updateUserRestaurantAddress(id, restaurant_address);
    }

    public void updateUserRestaurantID(String id, String restaurantID) {
        mUserManager.updateUserRestaurantID(id, restaurantID);
    }

    public void updateUserRestaurantDateChoice(String id, String restaurant_date_choice) {
        mUserManager.updateUserRestaurantDateChoice(id, restaurant_date_choice);
    }

    public void updateUserLikeRestaurant(String id, Integer starNumber) {
        mUserManager.updateUserLikeRestaurant(id, starNumber);
    }

    public void deleteUser(String id) {
        mUserManager.deleteUser(id);
    }

    public void getRestaurant(final Context context) {
        final PlacesClient placesClient = Places.createClient(context);
        List<Place.Field> arraylistField = new ArrayList<>();
        arraylistField.add(Place.Field.ID);
        arraylistField.add(Place.Field.TYPES);
        arraylistField.add(Place.Field.NAME);
        arraylistField.add(Place.Field.ADDRESS);
        arraylistField.add(Place.Field.PHOTO_METADATAS);
        arraylistField.add(Place.Field.LAT_LNG);

        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(arraylistField);

        if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener((OnCompleteListener<FindCurrentPlaceResponse>) task -> {
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
            });
        }
    }

    public void updateLocation(Location location) {
        liveLocation.setValue(location);
    }

    public void getWorkmates() {
        getUsersCollection(new UserRepository.WorkmatesListener() {
            @Override
            public void onWorkmatesSuccess(List<Workmate> workmates) {
                ArrayList<Workmate> workmatesArrayList = new ArrayList<>(workmates);
                liveWorkmates.setValue(workmatesArrayList);
                if (liveRestaurant.getValue() != null) {
                    if (Objects.requireNonNull(liveRestaurant.getValue()).size() > 0) {
                        mapWorkmatesToRestaurant();
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
                    boolean sameName = restaurant.getName().equals(workmate.getRestaurant());
                    boolean sameDate = workmate.getRestaurant_date_choice().equals(getReadableDate());
                    boolean containsList = isWorkmateInList(workmate, restaurant.getWorkmatesEatingHere());
                    if (sameName && sameDate && !containsList) {
                        restaurant.getWorkmatesEatingHere().add(workmate);
                    } else if (!sameName && containsList && sameDate) {
                        restaurant.setWorkmatesEatingHere(new ArrayList<>());
                    }
                }
            }
        }
        liveRestaurant.postValue(restaurants);
    }

    private boolean isWorkmateInList(Workmate workmateOne, List<Workmate> workmates) {
        boolean isInList = false;
        for (Workmate workmate : workmates) {
            if (workmateOne.getId().equals(workmate.getId())) {
                isInList = true;
            }
        }
        return isInList;
    }

    private String getReadableDate() {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY", Locale.getDefault());
        return formatter.format(now);
    }

    public void getAllLikes() {
        getLikesCollection(new UserRepository.LikesListener() {
            @Override
            public void onLikesSuccess(List<Like> likes) {
                liveLikes.setValue((ArrayList<Like>) likes);
            }
        });
    }

    public void getLikesForRestaurant(Restaurant restaurant) {
        getAllLikes();
        ArrayList<Like> likes = liveLikes.getValue();
        if (likes != null) {
            for (Like like : likes) {
                if (like.getRestaurantId().equals(restaurant.getId())) {
                    restaurant.getLikes().add(like);
                }
            }
        }
    }

    public FetchPlaceRequest getFetchPlaceRequest(String id) {
        List<Place.Field> detailsArraylistField = new ArrayList<>();
        detailsArraylistField.add(Place.Field.NAME);
        detailsArraylistField.add(Place.Field.TYPES);
        detailsArraylistField.add(Place.Field.ADDRESS);
        detailsArraylistField.add(Place.Field.PHOTO_METADATAS);
        detailsArraylistField.add(Place.Field.ID);
        detailsArraylistField.add(Place.Field.LAT_LNG);
        detailsArraylistField.add(Place.Field.OPENING_HOURS);
        detailsArraylistField.add(Place.Field.PHONE_NUMBER);
        detailsArraylistField.add(Place.Field.WEBSITE_URI);
        return FetchPlaceRequest.builder(id, detailsArraylistField)
                .build();
    }

    public void getRestaurantFromId(Context context, String id) {
        final PlacesClient placesClient = Places.createClient(context);
        placesClient.fetchPlace(getFetchPlaceRequest(id))
                .addOnSuccessListener(
                        new OnSuccessListener<FetchPlaceResponse>() {
                            @Override
                            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                                liveMyRestaurant.setValue(new Restaurant(fetchPlaceResponse.getPlace()));
                            }
                        });

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
                if (Objects.requireNonNull(period.getOpen()).getDay().equals(UserRepository.getCurrentDayOfWeek())) {
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
}