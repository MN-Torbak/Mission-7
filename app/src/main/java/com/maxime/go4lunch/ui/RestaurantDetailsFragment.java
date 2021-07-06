package com.maxime.go4lunch.ui;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.maxime.go4lunch.DrawerActivity;
import com.maxime.go4lunch.Notifications.MyNotificationPublisher;
import com.maxime.go4lunch.R;
import com.maxime.go4lunch.RestaurantFragmentAdapter;
import com.maxime.go4lunch.api.UserHelper;
import com.maxime.go4lunch.model.Like;
import com.maxime.go4lunch.model.Restaurant;
import com.maxime.go4lunch.model.Workmate;
import com.maxime.go4lunch.viewmodel.DrawerSharedViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        getActivity().findViewById(R.id.autocomplete_fragment).setVisibility(View.GONE);
        getActivity().findViewById(R.id.autocomplete_background).setVisibility(View.GONE);
        mSharedViewModel = new ViewModelProvider(this).get(DrawerSharedViewModel.class);
        mSharedViewModel.getWorkmates();
        mSharedViewModel.getAllLikes();
        mSharedViewModel.getRestaurant(requireActivity());
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        likeRestaurant = (Button) view.findViewById(R.id.like);


        final Bundle b = this.getArguments();
        if (b != null && b.getParcelable("restaurant") != null) {
            restaurantProfil = b.getParcelable("restaurant");
            displayRestaurantInformations();
            getAllWorkmatesWhoEatHere();
            observeWorkmate();
            scheduleNotification(getContext(), 1000, NOTIF_ID);
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
                                scheduleNotification(getContext(), 1000, NOTIF_ID);
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
    }


    public void scheduleNotification(Context context, long delay, int notificationId) {//delay is after how much time(in millis) from current time you want to schedule the notification
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationCompat.CATEGORY_EVENT)
                .setContentTitle(getTitle())
                .setContentText(getText())
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_checkbox_selected);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        notificationManager.notify(123, builder.build());

        Intent intent = new Intent(context, DrawerActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(activity);

        Notification notification = builder.build();

        Intent notificationIntent = new Intent(context, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);


    }


    private long getNextNotifTime() {
        return System.currentTimeMillis() + 10000;
    }

    private String getTitle() {
        return "Go4Lunch";
    }

    private String getText() {
        String workmatelist = "";
        if (restaurantProfil.getWorkmatesBeEating() != null && restaurantProfil != null && restaurantProfil.getWorkmatesBeEating().size() > 1 ) {
            for (Workmate workmate : restaurantProfil.getWorkmatesBeEating()) {
                workmatelist = workmate + workmate.getName();
            }
            return R.string.choice + restaurantProfil.getName() + " " + restaurantProfil.getAddress() + R.string.join + workmatelist;
        }
        return R.string.choice + restaurantProfil.getName() + " " + restaurantProfil.getAddress();
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

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "toto";
            String description = "tatra";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NotificationCompat.CATEGORY_EVENT, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void displayRestaurantInformations() {
        nameRestaurant.setText(restaurantProfil.getName());
        addressRestaurant.setText(restaurantProfil.getAddress());
        Glide.with(avatarRestaurant.getContext())
                .load(restaurantProfil.getUrlAvatar())
                .apply(RequestOptions.centerCropTransform())
                .into(avatarRestaurant);
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
