package com.maxime.go4lunch.ui.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.maxime.go4lunch.MainActivity;
import com.maxime.go4lunch.model.Workmate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.maxime.go4lunch.R;
import com.maxime.go4lunch.api.UserManager;
import com.maxime.go4lunch.viewmodel.SharedViewModel;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {

    ImageView avatar;
    EditText name;
    EditText avatarEdit;
    Button updateName;
    Button updateAvatar;
    Button notifications;
    Button signOut;
    Button delete;

    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    private static final int UPDATE_USERNAME = 30;
    private static final int UPDATE_USERAVATAR = 40;

    SharedViewModel mSharedViewModel;
    private SharedPreferences mPreferences;
    public static final String NOTIFICATION = "notification";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        avatar = (ImageView) view.findViewById(R.id.avatar);
        name = (EditText) view.findViewById(R.id.name);
        avatarEdit = (EditText) view.findViewById(R.id.avatar_edit);
        updateName = view.findViewById(R.id.button_update);
        updateAvatar = view.findViewById(R.id.button_update_avatar);
        notifications = view.findViewById(R.id.button_notification);
        signOut = view.findViewById(R.id.button_sign_out);
        delete = view.findViewById(R.id.button_delete);
        mPreferences = requireContext().getSharedPreferences(NOTIFICATION, MODE_PRIVATE);
        mSharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        this.updateUIWhenCreating();

        updateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUpdateButton();
            }
        });

        updateAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUpdateAvatarButton();
            }
        });

        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickNotifications();
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignOutButton();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDeleteButton();
            }
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void updateUIWhenCreating() {

        if (this.mSharedViewModel.getCurrentUser() != null) {

            if (this.mSharedViewModel.getCurrentUser().getPhotoUrl() == null) {
                Glide.with(requireContext())
                        .load("https://www.icône.com/images/icones/2/9/face-plain-2.png")
                        .apply(RequestOptions.circleCropTransform())
                        .into(avatar);
            }

            mSharedViewModel.getUser(this.mSharedViewModel.getCurrentUser().getUid(), new OnUserSuccessListener() {
                @Override
                public void onUserSuccess(Workmate currentWorkmate) {
                    assert currentWorkmate != null;
                    String username = TextUtils.isEmpty(currentWorkmate.getName()) ? getString(R.string.info_no_username_found) : currentWorkmate.getName();
                    name.setText(username);
                    String useravatar = TextUtils.isEmpty(currentWorkmate.getAvatar()) ? getString(R.string.info_no_useravatar_found) : currentWorkmate.getAvatar();
                    avatarEdit.setText(useravatar);
                    if (currentWorkmate.getAvatar() != null) {
                        Glide.with(requireContext())
                                .load(currentWorkmate.getAvatar())
                                .apply(RequestOptions.circleCropTransform())
                                .into(avatar);
                    }
                }
            });

            if (mPreferences.getBoolean("notification_boolean", true)) {
                notifications.setBackground(getResources().getDrawable(R.drawable.notification_check));
            } else if (mPreferences.getBoolean("notification_boolean", false)) {
                notifications.setBackground(getResources().getDrawable(R.drawable.notification_cross));
            }
        }
    }

    public interface OnUserSuccessListener {
        void onUserSuccess(Workmate workmate);
    }

    public void onClickUpdateButton() {
        this.updateUsernameInFirebase();
    }

    public void onClickUpdateAvatarButton() {
        this.updateUseravatarInFirebase();
    }

    public void onClickNotifications() {

        if (mPreferences.getBoolean("notification_boolean", true)) {
            mPreferences.edit().putBoolean("notification_boolean", false).apply();
            notifications.setBackground(getResources().getDrawable(R.drawable.notification_cross));
        } else {
            mPreferences.edit().putBoolean("notification_boolean", true).apply();
            notifications.setBackground(getResources().getDrawable(R.drawable.notification_check));
        }
    }

    public void onClickSignOutButton() {
        signOutUserFromFirebase();
    }

    public void onClickDeleteButton() {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.popup_message_confirmation_delete_account)
                .setPositiveButton(R.string.popup_message_choice_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteUserFromFirebase();
                    }
                })
                .setNegativeButton(R.string.popup_message_choice_no, null)
                .show();
    }

    private void updateUsernameInFirebase() {

        String username = this.name.getText().toString();

        if (this.mSharedViewModel.getCurrentUser() != null) {
            if (!username.isEmpty() && !username.equals(getString(R.string.info_no_username_found))) {
                mSharedViewModel.updateUserName(this.mSharedViewModel.getCurrentUser().getUid(), username)
                        .addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME));
            }
        }
    }

    private void updateUseravatarInFirebase() {

        String useravatar = this.avatarEdit.getText().toString();

        if (this.mSharedViewModel.getCurrentUser() != null) {
            if (!useravatar.isEmpty() && !useravatar.equals(getString(R.string.info_no_useravatar_found))) {
                mSharedViewModel.updateUserAvatar(this.mSharedViewModel.getCurrentUser().getUid(), useravatar)
                        .addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERAVATAR));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(requireContext())
                .addOnSuccessListener(requireActivity(), this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    private void deleteUserFromFirebase() {
        if (this.mSharedViewModel.getCurrentUser() != null) {
            mSharedViewModel.deleteUser(this.mSharedViewModel.getCurrentUser().getUid());
        }
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin) {
                    case UPDATE_USERNAME:
                        Toast.makeText(getContext(), getString(R.string.username_updated), Toast.LENGTH_LONG).show();
                        break;
                    case UPDATE_USERAVATAR:
                        Toast.makeText(getContext(), getString(R.string.useravatar_updated), Toast.LENGTH_LONG).show();
                        break;
                    case SIGN_OUT_TASK:
                        requireActivity().finish();
                        startMainActivity();
                        break;
                }
            }
        };
    }

    private void startMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

    protected OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            }
        };
    }

}