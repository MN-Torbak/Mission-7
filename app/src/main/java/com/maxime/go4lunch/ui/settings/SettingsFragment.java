package com.maxime.go4lunch.ui.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputEditText;
import com.maxime.go4lunch.model.Workmate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.maxime.go4lunch.R;
import com.maxime.go4lunch.api.UserHelper;

public class SettingsFragment extends Fragment {

    TextView name;
    TextView mail;
    Button update;
    Button signOut;
    Button delete;
    TextInputEditText textInputEditTextUsername;

    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    private static final int UPDATE_USERNAME = 30;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        name = (TextView) view.findViewById(R.id.name);
        mail = (TextView) view.findViewById(R.id.mail);
        update = view.findViewById(R.id.button_update);
        signOut = view.findViewById(R.id.button_sign_out);
        delete = view.findViewById(R.id.button_delete);
        this.updateUIWhenCreating();

        // --------------------
        // ACTIONS
        // --------------------

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUpdateButton();
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

    private void updateUIWhenCreating(){

        if (this.getCurrentUser() != null){

            /*if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageViewProfile);
            }*/

            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();

            this.mail.setText(email);

            // 7 - Get additional data from Firestore (isMentor & Username)
            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Workmate currentWorkmate = documentSnapshot.toObject(Workmate.class);
                    String username = TextUtils.isEmpty(currentWorkmate.getName()) ? getString(R.string.info_no_username_found) : currentWorkmate.getName();
                    /*textInputEditTextUsername.setText(username);*/
                }
            });
        }
    }

    public void onClickUpdateButton() {
        this.updateUsernameInFirebase();
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

    private void updateUsernameInFirebase(){

        String username = this.textInputEditTextUsername.getText().toString();

        if (this.getCurrentUser() != null){
            if (!username.isEmpty() &&  !username.equals(getString(R.string.info_no_username_found))){
                UserHelper.updateUserName(username, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME));
            }
        }
    }

    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(getContext())
                .addOnSuccessListener(getActivity(), this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    private void deleteUserFromFirebase(){
        if (this.getCurrentUser() != null) {
            UserHelper.deleteUser(this.getCurrentUser().getUid())/*.addOnFailureListener(getActivity().onFailureListener())*/;

        }
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    // 8 - Hiding Progress bar after request completed
                    case UPDATE_USERNAME:
                        /*progressBar.setVisibility(View.INVISIBLE);*/
                        break;
                }
            }
        };
    }


    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    protected Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    protected OnFailureListener onFailureListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            }
        };
    }

}