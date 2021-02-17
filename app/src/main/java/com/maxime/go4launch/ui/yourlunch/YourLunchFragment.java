package com.maxime.go4launch.ui.yourlunch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.maxime.go4launch.R;

import java.util.Objects;


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


