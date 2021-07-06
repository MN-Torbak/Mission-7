package com.maxime.go4lunch.Notifications;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.maxime.go4lunch.R;

public class NotificationsWorker extends Worker {
    public NotificationsWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        // Do the work here--in this case, upload the images.
        displayNotifications();

        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }

    private void displayNotifications() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "k")
                .setSmallIcon(R.drawable.ic_checkbox_selected)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                //.setContentIntent(pendingIntent)
                //.addAction(R.drawable.ic_snooze, getString(R.string.snooze),
                        //snoozePendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, builder.build());
    }
}
