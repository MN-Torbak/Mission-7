package com.maxime.go4lunch.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.maxime.go4lunch.R;
import com.maxime.go4lunch.RestaurantFragmentAdapter;
import com.maxime.go4lunch.api.UserHelper;
import com.maxime.go4lunch.model.Restaurant;
import com.maxime.go4lunch.model.Workmate;
import com.maxime.go4lunch.viewmodel.DrawerSharedViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RestaurantDetailsFragment extends Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView avatarRestaurant = view.findViewById(R.id.avatar_restaurant);
        TextView nameRestaurant = view.findViewById(R.id.name_restaurant);
        TextView addressRestaurant = view.findViewById(R.id.address_restaurant);
        mRecyclerView = view.findViewById(R.id.restaurantFragmentRecyclerView);
        getAllWorkmatesWhoEatHere();

        mSharedViewModel = new ViewModelProvider(this).get(DrawerSharedViewModel.class);
        mSharedViewModel.getRestaurant(requireActivity());
        mSharedViewModel.getWorkmates(requireActivity());
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        Bundle b = this.getArguments();
        if (b != null) {
            restaurantProfil = b.getParcelable("restaurant");
        }

        mSharedViewModel.liveWorkmates.observe(requireActivity(), new Observer<ArrayList<Workmate>>() {
            @Override
            public void onChanged(final ArrayList<Workmate> workmates) {
                for (Workmate workmate : workmates) {
                    if (workmate.getId().equals(Objects.requireNonNull(getCurrentUser()).getUid())) {
                        mWorkmate = workmate;

                        if (mWorkmate.getRestaurant().equals(restaurantProfil.getName())) {
                            choice = true;
                            fab.setImageResource(R.drawable.ic_checkbox_green);
                        } else {
                            choice = false;
                            fab.setImageResource(R.drawable.ic_checkbox_grey);
                        }
                    }
                }
            }
        });


        assert restaurantProfil != null;
        nameRestaurant.setText(restaurantProfil.getName());
        addressRestaurant.setText(restaurantProfil.getAddress());
        Glide.with(avatarRestaurant.getContext())
                .load(restaurantProfil.getUrlAvatar())
                .apply(RequestOptions.centerCropTransform())
                .into(avatarRestaurant);


        //Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_your_lunch);
        //toolbar.setVisibility(View.INVISIBLE);
        //setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.toolbar_layout);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choice) {
                    choice = false;
                    fab.setImageResource(R.drawable.ic_checkbox_grey);
                    UserHelper.updateUserRestaurant(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), "aucun");
                    restaurantProfil.getWorkmatesBeEating().remove(mWorkmate);
                    getAllWorkmatesWhoEatHere();
                } else {
                    choice = true;
                    fab.setImageResource(R.drawable.ic_checkbox_green);
                    UserHelper.updateUserRestaurant(mWorkmate.getId(), restaurantProfil.getName());
                    restaurantProfil.getWorkmatesBeEating().add(mWorkmate);
                    getAllWorkmatesWhoEatHere();
                }
            }
        });

    }

    private void displayWorkmates(List<Workmate> workmates) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        RestaurantFragmentAdapter restaurantFragmentAdapter = new RestaurantFragmentAdapter(workmates);
        restaurantFragmentAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(restaurantFragmentAdapter);
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
