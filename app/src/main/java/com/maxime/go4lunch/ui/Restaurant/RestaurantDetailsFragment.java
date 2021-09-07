package com.maxime.go4lunch.ui.Restaurant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.maxime.go4lunch.Notifications.NotificationsWorker;
import com.maxime.go4lunch.R;
import com.maxime.go4lunch.api.UserHelper;
import com.maxime.go4lunch.model.Like;
import com.maxime.go4lunch.model.Restaurant;
import com.maxime.go4lunch.model.Workmate;
import com.maxime.go4lunch.viewmodel.DrawerSharedViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RestaurantDetailsFragment extends Fragment {

    private static final int NOTIF_ID = 123;
    Restaurant restaurantProfil;
    Workmate mWorkmate;
    RecyclerView mRecyclerView;
    DrawerSharedViewModel mSharedViewModel;

    TextView nameRestaurant;
    ImageView avatarRestaurant;
    TextView addressRestaurant;
    Button phoneNumberRestaurant;
    Button likeRestaurant;
    Button webSiteRestaurant;
    ProgressBar mProgressBar;
    boolean choice;
    FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        avatarRestaurant = view.findViewById(R.id.avatar_restaurant);
        nameRestaurant = view.findViewById(R.id.name_restaurant);
        addressRestaurant = view.findViewById(R.id.address_restaurant);
        phoneNumberRestaurant = view.findViewById(R.id.phone_number);
        webSiteRestaurant = view.findViewById(R.id.website);
        mRecyclerView = view.findViewById(R.id.restaurantFragmentRecyclerView);
        mProgressBar = view.findViewById(R.id.progressBar);
        if (getActivity().findViewById(R.id.autocomplete_fragment) != null && getActivity().findViewById(R.id.autocomplete_fragment).getVisibility() == View.VISIBLE) {
            getActivity().findViewById(R.id.autocomplete_fragment).setVisibility(View.GONE);
        }
        if (getActivity().findViewById(R.id.autocomplete_background) != null && getActivity().findViewById(R.id.autocomplete_background).getVisibility() == View.VISIBLE) {
            getActivity().findViewById(R.id.autocomplete_background).setVisibility(View.GONE);
        }
        if (getActivity().findViewById(R.id.button_tri) != null && getActivity().findViewById(R.id.button_tri).getVisibility() == View.VISIBLE) {
            getActivity().findViewById(R.id.button_tri).setVisibility(View.GONE);
        }
        mSharedViewModel = new ViewModelProvider(this).get(DrawerSharedViewModel.class);
        mSharedViewModel.getWorkmates();
        mSharedViewModel.getAllLikes();
        mSharedViewModel.getRestaurant(requireActivity());
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        likeRestaurant = (Button) view.findViewById(R.id.like);

        ProgressBar spinner = new android.widget.ProgressBar(
                getContext(),
                null,
                android.R.attr.progressBarStyle);

        spinner.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);


        final Bundle b = this.getArguments();
        if (b != null && b.getString("restaurant") != null) {
            mSharedViewModel.getRestaurantFromId(getContext(), b.getString("restaurant"));
            mSharedViewModel.liveMyRestaurant.observe(requireActivity(), new Observer<Restaurant>() {
                @Override
                public void onChanged(final Restaurant restaurant) {
                    restaurantProfil = restaurant;
                    displayRestaurantInformations();
                    getAllWorkmatesWhoEatHere();
                    observeWorkmate();
                    if (mProgressBar.getVisibility() == View.VISIBLE) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });
        } else {
            mSharedViewModel.liveRestaurant.observe(requireActivity(), new Observer<ArrayList<Restaurant>>() {
                @Override
                public void onChanged(final ArrayList<Restaurant> restaurants) {
                    for (Restaurant restaurant : restaurants) {
                        if (b != null) {
                            if (Objects.equals(b.getString("restaurant"), restaurant.getName())) {
                                restaurantProfil = restaurant;
                                displayRestaurantInformations();
                                getAllWorkmatesWhoEatHere();
                                observeWorkmate();
                                if (mProgressBar.getVisibility() == View.VISIBLE) {
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    }
                }
            });
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choice) {
                    choice = false;
                    fab.setImageResource(R.drawable.ic_checkbox_not_selected);
                    UserHelper.updateUserRestaurant(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), "aucun");
                    UserHelper.updateUserRestaurantAddress(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), "unknow");
                    UserHelper.updateUserRestaurantID(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), "unknow");
                    restaurantProfil.getWorkmatesBeEating().remove(mWorkmate);
                    getAllWorkmatesWhoEatHere();
                    WorkManager.getInstance(requireContext()).cancelAllWorkByTag("Notif");
                } else {
                    choice = true;
                    fab.setImageResource(R.drawable.ic_checkbox_selected);
                    UserHelper.updateUserRestaurant(mWorkmate.getId(), restaurantProfil.getName());
                    UserHelper.updateUserRestaurantAddress(mWorkmate.getId(), restaurantProfil.getAddress());
                    UserHelper.updateUserRestaurantID(mWorkmate.getId(), restaurantProfil.getId());
                    Date now = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY", Locale.getDefault());
                    String result = formatter.format(now);
                    UserHelper.updateUserRestaurantDateChoice(mWorkmate.getId(), result);
                    restaurantProfil.getWorkmatesBeEating().add(mWorkmate);
                    getAllWorkmatesWhoEatHere();
                    createNotif();
                }
            }
        });

        likeRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog();
            }
        });

        phoneNumberRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + restaurantProfil.getPhoneNumber()));
                startActivity(intent);
            }
        });

        webSiteRestaurant.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(restaurantProfil.getWebSite());
                startActivity(intent);
            }
        });

    }

    public void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Rate the Restaurant")
                .setItems(R.array.dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                observeLike(1);
                                break;
                            case 1:
                                observeLike(2);
                                break;
                            case 2:
                                observeLike(3);
                                break;
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void observeWorkmate() {
        mSharedViewModel.liveWorkmates.observe(requireActivity(), new Observer<ArrayList<Workmate>>() {
            @Override
            public void onChanged(final ArrayList<Workmate> workmates) {
                for (Workmate workmate : workmates) {
                    if (workmate.getId().equals(Objects.requireNonNull(getCurrentUser()).getUid())) {
                        mWorkmate = workmate;

                        if (workmate.getRestaurant().equals(restaurantProfil.getName())) {
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
    }

    public OneTimeWorkRequest createNotif() {
        return new OneTimeWorkRequest.Builder(NotificationsWorker.class)
                .setInitialDelay(getMilliseconds(), TimeUnit.MILLISECONDS)
                .addTag("Notif")
                .build();
    }

    private long getMilliseconds() {
        //TODO: calculer les millisecondes entre maintenant et le prochain midi
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long now = System.currentTimeMillis();
        long todayMidday = calendar.getTimeInMillis();
        long difference = todayMidday - now;

        if (difference > 0) {
            return difference;
        } else {
            //TODO: notification à déclencher lendemain midi
            return difference + (24 * 60 * 60 * 1000);
        }
    }

    private void observeLike(final Integer intCase) {
        final String likeId = mWorkmate.getId() + restaurantProfil.getId();
        mSharedViewModel.liveLikes.observe(requireActivity(), new Observer<ArrayList<Like>>() {
            @Override
            public void onChanged(final ArrayList<Like> likes) {
                for (Like like : likes) {
                    if (like.getId().equals(likeId)) {
                        UserHelper.updateUserLikeRestaurant(likeId, intCase);
                    } else {
                        UserHelper.createLike(likeId, mWorkmate.getId(), restaurantProfil.getId());
                        UserHelper.updateUserLikeRestaurant(likeId, intCase);
                    }
                }
            }
        });
    }

    private void displayRestaurantInformations() {
        nameRestaurant.setText(restaurantProfil.getName());
        addressRestaurant.setText(restaurantProfil.getAddress());
        Glide.with(avatarRestaurant.getContext())
                .load(restaurantProfil.getUrlAvatar())
                .apply(RequestOptions.centerCropTransform())
                .into(avatarRestaurant);
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

    public List<Like> findLikes(Task<QuerySnapshot> task) {
        List<Like> likes = new ArrayList<>();
        for (DocumentSnapshot document : task.getResult()) {
            Like like = document.toObject(Like.class);
            assert like != null;
            if (like.getRestaurantId().equals(restaurantProfil.getId())) {
                likes.add(like);
            }
        }
        return likes;
    }

    public void getAllLikes() {
        UserHelper.getLikesCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    findLikes(task);
                }
            }
        });
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
