package com.maxime.go4lunch.ui.yourlunch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.maxime.go4lunch.DrawerActivity;
import com.maxime.go4lunch.MainActivity;
import com.maxime.go4lunch.R;
import com.maxime.go4lunch.model.Restaurant;
import com.maxime.go4lunch.model.Workmate;
import com.maxime.go4lunch.ui.mapview.MapViewFragment;
import com.maxime.go4lunch.viewmodel.DrawerSharedViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static androidx.navigation.fragment.NavHostFragment.findNavController;


public class YourLunchFragment extends Fragment {

    DrawerSharedViewModel mSharedViewModel;
    NavController mNavController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_your_lunch, container, false);

        NavHostFragment navHostBottomFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.nav_host_bottom_fragment);
        mNavController = navHostBottomFragment.getNavController();
        BottomNavigationView bottomNav = root.findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, mNavController);

        mSharedViewModel = new ViewModelProvider(this).get(DrawerSharedViewModel.class);
        mSharedViewModel.getWorkmates();
        observeWorkmate(root);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        assert autocompleteFragment != null;
        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);
        autocompleteFragment.setCountry("FR");
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.PHOTO_METADATAS));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Bundle b = new Bundle();
                b.putString("restaurant", new Restaurant(place).getId());
                mNavController.navigate(R.id.action_navigation_map_view_to_restaurantDetailsFragment, b);
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
            }
        });

        Button buttonTri = root.findViewById(R.id.button_tri);
        buttonTri.setVisibility(View.GONE);
        Spinner tri_spinner = root.findViewById(R.id.tri_spinner);
        buttonTri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tri_spinner.setVisibility(View.INVISIBLE);
                tri_spinner.performClick();
                ArrayAdapter<CharSequence> adapterLocation = ArrayAdapter.createFromResource(getContext(),
                        R.array.tri_spinner, android.R.layout.simple_spinner_item);
                adapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tri_spinner.setAdapter(adapterLocation);
            }
        });
        return root;

    }


    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private void observeWorkmate(View root) {
        Bundle b = this.getArguments();
        if (b != null && b.getString("restaurant") != null) {
            mNavController.navigate(R.id.action_navigation_map_view_to_restaurantDetailsFragment, b);
            ((DrawerActivity) requireActivity()).closeDrawer();
        }
    }


}




