package com.maxime.go4launch;

import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.maxime.go4launch.api.UserHelper;
import com.maxime.go4launch.model.Restaurant;
import com.maxime.go4launch.model.Workmate;

public class RestaurantActivity extends AppCompatActivity {

    Restaurant restaurant;
    Workmate mWorkmate;

    ImageView avatarRestaurant;
    TextView nameRestaurant;
    TextView infoRestaurant;
    TextView addressRestaurant;
    Button phoneNumberRestaurant;
    Button likeRestaurant;
    Button webSiteRestaurant;
    boolean like = false;
    boolean choice = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(restaurant.getName());



        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        choice = restaurant.getName().equals(mWorkmate.getRestaurant());
        if (choice) {
            fab.setImageResource(R.drawable.ic_checkbox_green);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choice) {
                    choice = false;
                    fab.setImageResource(R.drawable.ic_checkbox_grey);
                    UserHelper.updateUserRestaurant(FirebaseAuth.getInstance().getCurrentUser().getUid(), "no choice");
                } else {
                    choice = true;
                    fab.setImageResource(R.drawable.ic_checkbox_green);
                    UserHelper.updateUserRestaurant(mWorkmate.getId(), restaurant.getName());
                }
            }
        });

    }
}