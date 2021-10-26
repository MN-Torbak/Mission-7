package com.maxime.go4lunch.Notifications;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.maxime.go4lunch.R;
import com.maxime.go4lunch.api.UserManager;
import com.maxime.go4lunch.api.UserRepository;
import com.maxime.go4lunch.model.Workmate;
import com.maxime.go4lunch.ui.settings.SettingsFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NotificationsWorker extends Worker {

    UserManager mUserManager;

    public NotificationsWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        mUserManager = new UserManager();
    }

    @NonNull
    @Override
    public Result doWork() {

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(SettingsFragment.NOTIFICATION, Context.MODE_PRIVATE);
        if (preferences.getBoolean("notification_boolean", true)) {
            getUser();
            return Result.success();
        }
        return Result.failure();
    }

    private void displayNotifications(String restaurantName, String restaurantAddress, String workmateWhoJoined) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "k")
                .setSmallIcon(R.drawable.ic_your_lunch)
                .setContentTitle("Go4Lunch")
                .setContentText(getText(restaurantName, restaurantAddress, workmateWhoJoined))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        notificationManager.notify(0, builder.build());
    }

    private String getReadableDate() {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY", Locale.getDefault());
        return formatter.format(now);
    }

    private void getUser() {
        mUserManager.getUser(Objects.requireNonNull(getCurrentUser()).getUid(), new UserRepository.OnUserSuccessListener() {
            @Override
            public void onUserSuccess(Workmate workmate) {
                getAllInformationsForNotifications(workmate.getRestaurant(), workmate.getRestaurant_address(), workmate.getRestaurant_date_choice());
            }
        });
    }

    private void checkDateAndDisplayNotifications(String restaurantName, String restaurantAddress, String restaurantDate, String workmateWhoJoined ) {
        if (getReadableDate().equals(restaurantDate)) {
            displayNotifications(restaurantName, restaurantAddress, workmateWhoJoined);
        }
    }

    private String getAllWorkmateWhoJoined(List<Workmate> workmates, String restaurantName) {
        String workmateList = "";
        for (Workmate workmate : workmates) {
            if (workmate.getRestaurant().equals(restaurantName)) {
                workmateList = workmate + workmate.getName();
            }
        }
        return workmateList;
    }

    private void getAllInformationsForNotifications(String restaurantName, String restaurantAddress, String restaurantDate) {
        mUserManager.getUsersCollection(new UserRepository.WorkmatesListener() {
            @Override
            public void onWorkmatesSuccess(List<Workmate> workmates) {
                String workmateWhoJoined = getAllWorkmateWhoJoined(workmates, restaurantName);
                checkDateAndDisplayNotifications(restaurantName, restaurantAddress, restaurantDate, workmateWhoJoined);
            }
        });
    }

    public String getText(String restaurant, String restaurant_address, String workmatelist) {

        //TODO: faire ce string
        return getApplicationContext().getString(R.string.choice) + restaurant + " " + restaurant_address + getApplicationContext().getString(R.string.join) + workmatelist;
    }


    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
