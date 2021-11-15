package com.maxime.go4lunch.ui.listview;

import android.location.Location;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maxime.go4lunch.R;
import com.maxime.go4lunch.model.Restaurant;
import com.maxime.go4lunch.viewmodel.SharedViewModel;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;


public class ListViewFragment extends Fragment {

    public static Location mLocation;
    RecyclerView mRecyclerView;
    ListViewAdapter listViewAdapter;
    Spinner mTriSpinner;

    @NonNull
    public SortMethod sortMethod = SortMethod.NONE;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_list_view, container, false);
        mRecyclerView = root.findViewById(R.id.fragmentListViewRecyclerView);
        DividerItemDecoration itemDecor = new DividerItemDecoration(requireContext(), VERTICAL);
        mRecyclerView.addItemDecoration(itemDecor);
        return root;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.liveRestaurant.observe(requireActivity(), new Observer<ArrayList<Restaurant>>() {
            @Override
            public void onChanged(ArrayList<Restaurant> restaurants) {
                displayListView(restaurants);
                for (Restaurant restaurant : restaurants) {
                    sharedViewModel.getLikesForRestaurant(restaurant);
                }
            }
        });
        sharedViewModel.liveLocation.observe(requireActivity(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                mLocation = location;
            }
        });
        requireActivity().findViewById(R.id.autocomplete_fragment).setVisibility(View.VISIBLE);
        requireActivity().findViewById(R.id.autocomplete_background).setVisibility(View.VISIBLE);
        mTriSpinner = requireActivity().findViewById(R.id.tri_spinner);
        requireActivity().findViewById(R.id.tri_spinner).setVisibility(View.INVISIBLE);
        requireActivity().findViewById(R.id.button_tri).setVisibility(View.VISIBLE);

        mTriSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
               if (position == 1) {
                   sortMethod = SortMethod.PROXI;
                } else if (position == 2) {
                   sortMethod = SortMethod.STARS;
               } else if (position == 3) {
                   sortMethod = SortMethod.NUMBEROFWORKMATES;
               }
                listViewAdapter.updateFilter(sortMethod, mLocation);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                sortMethod = SortMethod.NONE;
                listViewAdapter.updateFilter(sortMethod, mLocation);
            }
        });
    }


    private void displayListView(List<Restaurant> restaurants) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        listViewAdapter = new ListViewAdapter(restaurants);
        listViewAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(listViewAdapter);
    }

    public enum SortMethod {
        PROXI,
        STARS,
        NUMBEROFWORKMATES,
        NONE
    }
}





