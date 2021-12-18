package com.maxime.go4lunch.ui.workmates;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maxime.go4lunch.R;
import com.maxime.go4lunch.api.UserRepository;
import com.maxime.go4lunch.model.Workmate;
import com.maxime.go4lunch.viewmodel.SharedViewModel;

import java.util.List;

import static android.widget.LinearLayout.VERTICAL;

public class WorkmatesFragment extends Fragment {

    RecyclerView mRecyclerView;
    SharedViewModel mSharedViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_workmates, container, false);
        mRecyclerView = root.findViewById(R.id.fragmentWorkmatesRecyclerView);
        mSharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        getAllDocs();
        DividerItemDecoration itemDecor = new DividerItemDecoration(requireContext(), VERTICAL);
        mRecyclerView.addItemDecoration(itemDecor);
        requireActivity().findViewById(R.id.autocomplete_fragment).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.autocomplete_background).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.tri_spinner).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.button_tri).setVisibility(View.GONE);
        return root;
    }

    private void displayWorkmates(List<Workmate> workmates) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        WorkmateAdapter workmateAdapter = new WorkmateAdapter(workmates);
        workmateAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(workmateAdapter);
    }

    private void getAllDocs() {
        mSharedViewModel.getUsersCollection(this::displayWorkmates);
    }
}





