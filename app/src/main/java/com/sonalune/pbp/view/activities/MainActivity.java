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

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_nav);

        // Load default fragment
        loadFragment(new HomeFragment());

        // Set nav listener
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                //                case R.id.nav_playlist:
//                    selectedFragment = new PlaylistFragment();
//                    break;
//                case R.id.nav_capsule:
//                    selectedFragment = new CapsuleFragment();
//                    break;
            }
// ubah nanti
//            switch (item.getItemId()) {
//                case R.id.nav_home:
//                    selectedFragment = new HomeFragment();
//                    break;
////                case R.id.nav_playlist:
////                    selectedFragment = new PlaylistFragment();
////                    break;
////                case R.id.nav_capsule:
////                    selectedFragment = new CapsuleFragment();
////                    break;
//            }

            return loadFragment(selectedFragment);
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