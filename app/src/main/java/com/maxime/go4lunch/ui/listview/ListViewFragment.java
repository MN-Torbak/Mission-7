package com.maxime.go4lunch.ui.listview;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.maxime.go4lunch.DrawerActivity;
import com.maxime.go4lunch.R;
import com.maxime.go4lunch.model.Restaurant;
import com.maxime.go4lunch.viewmodel.DrawerSharedViewModel;

import java.util.ArrayList;
import java.util.List;



public class ListViewFragment extends Fragment {

    private DrawerSharedViewModel mSharedViewModel;
    RecyclerView mRecyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_list_view, container, false);
        mRecyclerView = root.findViewById(R.id.fragmentListViewRecyclerView);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSharedViewModel = new ViewModelProvider(requireActivity()).get(DrawerSharedViewModel.class);
        List<Restaurant> restaurants = new ArrayList<>();
        displayListView(listRestaurant(restaurants)); //TODO listen to livedata to display restaurants
    }

    private void displayListView(List<Restaurant> restaurants) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        ListViewAdapter listViewAdapter = new ListViewAdapter(restaurants);
        listViewAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(listViewAdapter);
    }

    public List<Restaurant> listRestaurant(List<Restaurant> restaurants) {
        PlacesClient placesClient = Places.createClient(requireActivity());
        ((DrawerActivity)getActivity()).getRestaurant(placesClient);
        return restaurants;
    }


}





