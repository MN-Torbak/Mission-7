package com.maxime.go4lunch.ui.yourlunch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.maxime.go4lunch.R;


public class YourLunchFragment extends Fragment {

private YourLunchViewModel mYourLunchViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mYourLunchViewModel =
                ViewModelProviders.of(this).get(YourLunchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_your_lunch, container, false);

        NavHostFragment navHostBottomFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.nav_host_bottom_fragment);
        NavController navController = navHostBottomFragment.getNavController();
        BottomNavigationView bottomNav = root.findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navController);
        return root;
    }

}


