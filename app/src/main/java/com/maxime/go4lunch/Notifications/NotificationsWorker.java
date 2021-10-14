package com.maxime.go4lunch.Notifications;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.maxime.go4lunch.R;
import com.maxime.go4lunch.api.UserManager;
import com.maxime.go4lunch.model.Workmate;
import com.maxime.go4lunch.ui.settings.SettingsFragment;
import com.maxime.go4lunch.viewmodel.SharedViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NotificationsWorker extends Worker {

    private SharedPreferences mPreferences;
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

        mPreferences = getApplicationContext().getSharedPreferences(SettingsFragment.NOTIFICATION, Context.MODE_PRIVATE);
        if (mPreferences.getBoolean("notification_boolean", true)) {
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
        DocumentReference docRef = UserManager.getUsersCollection().document(Objects.requireNonNull(getCurrentUser()).getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        getAllDocs(document.getString("restaurant"), document.getString("restaurant_address"), document.getString("restaurant_date_choice"));
                    }
                }
            }

        });
    }

    private void getNotificationsOn(String restaurantName, String restaurantAddress, String restaurantDate, String workmateWhoJoined ) {
        if (getReadableDate().equals(restaurantDate)) {
            displayNotifications(restaurantName, restaurantAddress, workmateWhoJoined);
        }
    }

    private List<Workmate> getAllWorkmates(Task<QuerySnapshot> task) {
        List<Workmate> workmates = new ArrayList<>();
        for (DocumentSnapshot document : task.getResult()) {
            Workmate workmate = document.toObject(Workmate.class);
            assert workmate != null;
            workmates.add(workmate);
        }
        return workmates;
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

    private void getAllDocs(String restaurantName, String restaurantAddress, String restaurantDate) {
        UserManager.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String workmateWhoJoined = getAllWorkmateWhoJoined(getAllWorkmates(task), restaurantName);
                    getNotificationsOn(restaurantName, restaurantAddress, restaurantDate, workmateWhoJoined);
                }
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
