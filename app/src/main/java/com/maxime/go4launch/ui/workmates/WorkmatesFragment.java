package com.maxime.go4launch.ui.workmates;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.maxime.go4launch.R;
import com.maxime.go4launch.api.UserHelper;
import com.maxime.go4launch.model.Workmate;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public class WorkmatesFragment extends Fragment {


    RecyclerView mRecyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_workmates, container, false);
        mRecyclerView = root.findViewById(R.id.fragmentWorkmatesRecyclerView);
        List<Workmate> workmates = new ArrayList<>();
        Workmate mate = new Workmate("1", "R.drawable.com_facebook_auth_dialog_background", "Jacque");
        workmates.add(mate);
        Workmate mate1 = new Workmate("2", "R.drawable.com_facebook_auth_dialog_background", "Jacque");
        workmates.add(mate1);

        Task<DocumentSnapshot> task = UserHelper.getUser("Wbl68CXjJvffJ9ReKhrG9Xy22xu1");

        UserHelper.getUser("Wbl68CXjJvffJ9ReKhrG9Xy22xu1").addOnFailureListener(onFailureListener()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Workmate currentWorkmate = documentSnapshot.toObject(Workmate.class);
                String username = TextUtils.isEmpty(currentWorkmate.getName()) ? getString(R.string.info_no_username_found) : currentWorkmate.getName();
                /*textInputEditTextUsername.setText(username);*/
            }
        });


        displayWorkmates(workmates);
        return root;
    }

    private void displayWorkmates(List<Workmate> workmates) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        WorkmateAdapter workmateAdapter = new WorkmateAdapter(workmates);
        workmateAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(workmateAdapter);
    }

    protected OnFailureListener onFailureListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            }
        };
    }

}