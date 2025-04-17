package com.sonalune.pbp.view.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sonalune.pbp.R;
import com.sonalune.pbp.view.fragments.HomeFragment;
import com.sonalune.pbp.view.fragments.PlaylistContent;
import com.sonalune.pbp.view.ui_components.NavBar;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find NavBar
        NavBar navBar = findViewById(R.id.navbar);

        // Load default fragment
        loadFragment(new HomeFragment());

        // Set NavBar listener
        navBar.setNavBarListener(tab -> {
            Fragment selectedFragment = null;

            switch (tab) {
                case "home":
                    selectedFragment = new HomeFragment();
                    break;
                case "capsuled":
                    selectedFragment = new PlaylistContent(); // Replace with CapsuleFragment if needed
                    break;
                case "profile":
                    // Add logic for profile tab
                    break;
            }

            loadFragment(selectedFragment);
        });
    }
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}