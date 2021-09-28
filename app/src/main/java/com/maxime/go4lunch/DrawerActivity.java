package com.maxime.go4lunch;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.maxime.go4lunch.model.Restaurant;
import com.maxime.go4lunch.model.Workmate;
import com.maxime.go4lunch.ui.Restaurant.RestaurantDetailsFragment;
import com.maxime.go4lunch.ui.settings.SettingsFragment;
import com.maxime.go4lunch.ui.yourlunch.YourLunchFragment;
import com.maxime.go4lunch.viewmodel.DrawerSharedViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Objects;

public class DrawerActivity extends AppCompatActivity {

    private static final int SIGN_OUT_TASK = 10;
    private AppBarConfiguration mAppBarConfiguration;
    DrawerSharedViewModel mDrawerSharedViewModel;
    public TextView Name;
    public TextView Mail;
    public ImageView Avatar;
    DrawerLayout drawer;
    Workmate mWorkmate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        Mail = findViewById(R.id.drawer_mail);
        Avatar = findViewById(R.id.drawer_avatar);
        Places.initialize(this, "AIzaSyC5PnYLjeSjD1CHBrujXoKqUt0yozB86bk");
        mDrawerSharedViewModel = new ViewModelProvider(this).get(DrawerSharedViewModel.class);
        mDrawerSharedViewModel.getRestaurant(this);
        mDrawerSharedViewModel.getWorkmates();
        mDrawerSharedViewModel.getAllLikes();
        NavigationView navigationView = findViewById(R.id.nav_drawer_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.drawer_name);
        TextView navUserMail = (TextView) headerView.findViewById(R.id.drawer_mail);
        ImageView navAvatar = (ImageView) headerView.findViewById(R.id.drawer_avatar);
        getWorkmate(navAvatar, navUsername, navUserMail);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_your_lunch, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.getMenu().findItem(R.id.nav_your_lunch).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                observeWorkmate(mWorkmate);
                return true;
            }
        });
        navigationView.getMenu().findItem(R.id.nav_settings).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                SettingsFragment settingsFragment = new SettingsFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, settingsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                drawer.closeDrawers();
                return true;
            }
        });
        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                signOutUserFromFirebase();
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    private void getWorkmate(ImageView avatar, TextView name, TextView mail) {
        mDrawerSharedViewModel.liveWorkmates.observe(this, new Observer<ArrayList<Workmate>>() {
            @Override
            public void onChanged(final ArrayList<Workmate> workmates) {
                for (Workmate workmate : workmates) {
                    if (workmate.getId().equals(Objects.requireNonNull(getCurrentUser()).getUid())) {
                        mWorkmate = workmate;
                        Glide.with(avatar.getContext())
                                .load((mWorkmate.getAvatar()))
                                .apply(RequestOptions.circleCropTransform())
                                .into(avatar);
                        name.setText(mWorkmate.getName());
                        mail.setText(getCurrentUser().getEmail());
                    }
                }
            }
        });
    }

    private void observeWorkmate(Workmate workmate) {
        if (!workmate.getRestaurant().equals("aucun")) {
            Bundle b = new Bundle();
            b.putString("restaurant", workmate.getRestaurantID());
            YourLunchFragment yourLunchFragment = new YourLunchFragment();
            yourLunchFragment.setArguments(b);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, yourLunchFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            closeDrawer();
        }
    }

    public void closeDrawer() {
        drawer.closeDrawers();
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