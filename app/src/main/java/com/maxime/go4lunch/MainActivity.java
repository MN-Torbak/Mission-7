package com.maxime.go4lunch;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.maxime.go4lunch.Notifications.NotificationsWorker;
import com.maxime.go4lunch.viewmodel.SharedViewModel;

import java.util.Arrays;

import static com.firebase.ui.auth.ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT;
import static com.firebase.ui.auth.ErrorCodes.DEVELOPER_ERROR;
import static com.firebase.ui.auth.ErrorCodes.EMAIL_LINK_CROSS_DEVICE_LINKING_ERROR;
import static com.firebase.ui.auth.ErrorCodes.EMAIL_LINK_DIFFERENT_ANONYMOUS_USER_ERROR;
import static com.firebase.ui.auth.ErrorCodes.EMAIL_LINK_PROMPT_FOR_EMAIL_ERROR;
import static com.firebase.ui.auth.ErrorCodes.EMAIL_LINK_WRONG_DEVICE_ERROR;
import static com.firebase.ui.auth.ErrorCodes.EMAIL_MISMATCH_ERROR;
import static com.firebase.ui.auth.ErrorCodes.ERROR_GENERIC_IDP_RECOVERABLE_ERROR;
import static com.firebase.ui.auth.ErrorCodes.ERROR_USER_DISABLED;
import static com.firebase.ui.auth.ErrorCodes.INVALID_EMAIL_LINK_ERROR;
import static com.firebase.ui.auth.ErrorCodes.NO_NETWORK;
import static com.firebase.ui.auth.ErrorCodes.PLAY_SERVICES_UPDATE_CANCELLED;
import static com.firebase.ui.auth.ErrorCodes.PROVIDER_ERROR;
import static com.firebase.ui.auth.ErrorCodes.UNKNOWN_ERROR;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    SharedViewModel mSharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        mSharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        if (this.isCurrentUserLogged()) { this.startDrawerActivity(); }
        else { this.startSignInActivity(); }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void createUserInFirestore() {

        if (this.mSharedViewModel.getCurrentUser() != null) {

            String id = this.mSharedViewModel.getCurrentUser().getUid();
            String avatar = (this.mSharedViewModel.getCurrentUser().getPhotoUrl() != null) ? this.mSharedViewModel.getCurrentUser().getPhotoUrl().toString() : "https://www.icône.com/images/icones/2/9/face-plain-2.png";
            String name = this.mSharedViewModel.getCurrentUser().getDisplayName();

            mSharedViewModel.createUser(id, avatar, name).addOnFailureListener(this.onFailureListener());
        }
    }

    private void startSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(), //EMAIL
                                        new AuthUI.IdpConfig.GoogleBuilder().build(), //GOOGLE
                                        new AuthUI.IdpConfig.FacebookBuilder().build())) // FACEBOOK
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_go4lunch)
                        .build(),
                RC_SIGN_IN);
    }

    private void startDrawerActivity() {
        Intent intent = new Intent(this, DrawerActivity.class);
        startActivity(intent);
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                if (response!=null && response.isNewUser()) {
                    this.createUserInFirestore();
                }
                startDrawerActivity();
            } else {
                if (response!=null && response.getError()!=null) {
                    Toast.makeText(getApplicationContext(), toFriendlyMessage(response.getError().getErrorCode()), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    protected OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            }
        };
    }

    public void notifications() {
        WorkRequest uploadWorkRequest =
                new OneTimeWorkRequest.Builder(NotificationsWorker.class)
                        .build();
        WorkManager
                .getInstance(this)
                .enqueue(uploadWorkRequest);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.close);
            String description = getString(R.string.chosen);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("k", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    protected Boolean isCurrentUserLogged() {
        return (this.mSharedViewModel.getCurrentUser() != null);
    }

    public static int toFriendlyMessage(@ErrorCodes.Code int code) {
        switch (code) {
            case UNKNOWN_ERROR:
                return R.string.unknown_error;
            case NO_NETWORK:
                return R.string.no_internet_connection;
            case PLAY_SERVICES_UPDATE_CANCELLED:
                return R.string.play_services_update_cancelled;
            case DEVELOPER_ERROR:
                return R.string.developer_error;
            case PROVIDER_ERROR:
                return R.string.provider_error;
            case ANONYMOUS_UPGRADE_MERGE_CONFLICT:
                return R.string.user_account_merge_conflict;
            case EMAIL_MISMATCH_ERROR:
                return R.string.attempting_to_sign_in_different_email;
            case INVALID_EMAIL_LINK_ERROR:
                return R.string.attempting_to_sign_with_an_invalid_email_link;
            case EMAIL_LINK_PROMPT_FOR_EMAIL_ERROR:
                return R.string.please_enter_your_email_to_continue_signing_in;
            case EMAIL_LINK_WRONG_DEVICE_ERROR:
                return R.string.you_must_open_the_email_link_on_the_same_device;
            case EMAIL_LINK_CROSS_DEVICE_LINKING_ERROR:
                return R.string.you_must_determine_if_you_want_continue_linking_or_complete_the_sign_in;
            case EMAIL_LINK_DIFFERENT_ANONYMOUS_USER_ERROR:
                return R.string.the_session_associated_with_this_sign_in_request_has_either_expired_or_was_cleared;
            case ERROR_USER_DISABLED:
                return R.string.the_user_account_has_been_disabled_by_an_administrator;
            case ERROR_GENERIC_IDP_RECOVERABLE_ERROR:
                return R.string.generic_idp_recoverable_error;
            default:
                throw new IllegalArgumentException(String.valueOf(R.string.unknown_code + code));
        }
    }
}