package com.maxime.go4lunch;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.maxime.go4lunch.api.UserHelper;
import com.maxime.go4lunch.model.Restaurant;
import com.maxime.go4lunch.model.Workmate;
import com.maxime.go4lunch.viewmodel.DrawerSharedViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RestaurantActivity extends AppCompatActivity {

    Restaurant restaurantProfil;
    Workmate mWorkmate;
    RecyclerView mRecyclerView;
    DrawerSharedViewModel mSharedViewModel;

    ImageView avatarRestaurant;
    TextView scheduleRestaurant;
    Button phoneNumberRestaurant;
    Button likeRestaurant;
    Button webSiteRestaurant;
    boolean like = false;
    boolean choice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_restaurant);
        ImageView avatarRestaurant = findViewById(R.id.avatar_restaurant);
        TextView nameRestaurant = findViewById(R.id.name_restaurant);
        TextView addressRestaurant = findViewById(R.id.address_restaurant);
        mRecyclerView = findViewById(R.id.restaurantFragmentRecyclerView);
        getAllWorkmatesWhoEatHere();

        mSharedViewModel = new ViewModelProvider(this).get(DrawerSharedViewModel.class);
        mSharedViewModel.getRestaurant(this);
        mSharedViewModel.getWorkmates();
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (getIntent().getExtras() == null) {
            finish();
        } else {
            restaurantProfil = getIntent().getExtras().getParcelable("restaurant");
        }

        mSharedViewModel.liveWorkmates.observe(this, new Observer<ArrayList<Workmate>>() {
            @Override
            public void onChanged(final ArrayList<Workmate> workmates) {
                for (Workmate workmate : workmates) {
                    if (workmate.getId().equals(Objects.requireNonNull(getCurrentUser()).getUid())) {
                        mWorkmate = workmate;

                        if (mWorkmate.getRestaurant().equals(restaurantProfil.getName())) {
                            choice = true;
                            fab.setImageResource(R.drawable.ic_checkbox_selected);
                        } else {
                            choice = false;
                            fab.setImageResource(R.drawable.ic_checkbox_not_selected);
                        }
                    }
                }
            }
        });


        assert restaurantProfil != null;
        nameRestaurant.setText(restaurantProfil.getName());
        addressRestaurant.setText(restaurantProfil.getAddress());


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choice) {
                    choice = false;
                    fab.setImageResource(R.drawable.ic_checkbox_not_selected);
                    UserHelper.updateUserRestaurant(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), "aucun");
                    restaurantProfil.getWorkmatesBeEating().remove(mWorkmate);
                    getAllWorkmatesWhoEatHere();
                } else {
                    choice = true;
                    fab.setImageResource(R.drawable.ic_checkbox_selected);
                    UserHelper.updateUserRestaurant(mWorkmate.getId(), restaurantProfil.getName());
                    restaurantProfil.getWorkmatesBeEating().add(mWorkmate);
                    getAllWorkmatesWhoEatHere();
                }
            }
        });

    }

    private void displayWorkmates(List<Workmate> workmates) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        RestaurantFragmentAdapter restaurantActivityAdapter = new RestaurantFragmentAdapter(workmates);
        restaurantActivityAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(restaurantActivityAdapter);
    }

    private List<Workmate> findWorkmates(Task<QuerySnapshot> task) {
        List<Workmate> workmates = new ArrayList<>();
        for (DocumentSnapshot document : task.getResult()) {
            Workmate workmate = document.toObject(Workmate.class);
            assert workmate != null;
            if (workmate.getRestaurant().equals(restaurantProfil.getName())) {
                workmates.add(workmate);
            }
        }
        return workmates;
    }

    public void getAllWorkmatesWhoEatHere() {
        UserHelper.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    displayWorkmates(findWorkmates(task));
                }
            }
        });
    }

    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}