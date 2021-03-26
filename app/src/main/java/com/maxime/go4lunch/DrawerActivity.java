package com.maxime.go4lunch;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.maxime.go4lunch.model.Restaurant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class DrawerActivity extends AppCompatActivity {

    private static final int SIGN_OUT_TASK = 10;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_drawer_view);
        Places.initialize(this, "AIzaSyC5PnYLjeSjD1CHBrujXoKqUt0yozB86bk");
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_your_lunch, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                signOutUserFromFirebase();
                return true;
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this).addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin) {
                    case SIGN_OUT_TASK:
                        finish();
                        startMainActivity();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public void getRestaurant(PlacesClient placesClient) {
        List<Place.Field> arraylistField = new ArrayList<>();
        arraylistField.add(Place.Field.TYPES);
        arraylistField.add(Place.Field.NAME);
        arraylistField.add(Place.Field.ADDRESS);
        arraylistField.add(Place.Field.PHOTO_METADATAS);

        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(arraylistField);

        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        FindCurrentPlaceResponse response = (FindCurrentPlaceResponse) task.getResult();
                        List<Restaurant> restaurants = new ArrayList<>();
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

    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    protected Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    protected OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            }
        };
    }
}