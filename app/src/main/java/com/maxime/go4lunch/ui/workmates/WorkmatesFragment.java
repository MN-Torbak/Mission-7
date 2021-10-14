package com.maxime.go4lunch.ui.workmates;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.maxime.go4lunch.R;
import com.maxime.go4lunch.api.UserManager;
import com.maxime.go4lunch.model.Workmate;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;

public class WorkmatesFragment extends Fragment {


    RecyclerView mRecyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_workmates, container, false);
        mRecyclerView = root.findViewById(R.id.fragmentWorkmatesRecyclerView);
        getAllDocs();
        DividerItemDecoration itemDecor = new DividerItemDecoration(requireContext(), VERTICAL);
        mRecyclerView.addItemDecoration(itemDecor);
        getActivity().findViewById(R.id.autocomplete_fragment).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.autocomplete_background).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.tri_spinner).setVisibility(View.GONE);
        getActivity().findViewById(R.id.button_tri).setVisibility(View.GONE);
        return root;
    }

    private void displayWorkmates(List<Workmate> workmates) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        WorkmateAdapter workmateAdapter = new WorkmateAdapter(workmates);
        workmateAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(workmateAdapter);
    }

    private List<Workmate> orderWorkmates (Task<QuerySnapshot> task) {
        List<Workmate> workmates = new ArrayList<>();
        for (DocumentSnapshot document : task.getResult()) {
            Workmate workmate = document.toObject(Workmate.class);
            assert workmate != null;
            if (workmate.getRestaurant().equals("aucun")) {
                workmates.add(workmates.size(), workmate);
            } else {
                workmates.add(0, workmate);
            }
        }
        return workmates;
    }

    private void getAllDocs() {
        UserManager.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    displayWorkmates(orderWorkmates(task));
                } else {
                    Log.d("WorkmatesFragment", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}





