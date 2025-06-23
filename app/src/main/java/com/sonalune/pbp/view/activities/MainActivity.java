package com.sonalune.pbp.view.activities;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.sonalune.pbp.R;
import com.sonalune.pbp.controller.SongController;
import com.sonalune.pbp.model.Singer;
import com.sonalune.pbp.model.Song;
import com.sonalune.pbp.view.fragments.CapsuleFragment;
import com.sonalune.pbp.view.fragments.HomeFragment;
import com.sonalune.pbp.view.fragments.PlayingScreen;
import com.sonalune.pbp.view.fragments.PlaylistContent;
import com.sonalune.pbp.view.fragments.Profile;
import com.sonalune.pbp.view.ui_components.NavBar;
import com.sonalune.pbp.view.ui_components.PlayerCardView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        PlaylistContent.OnSongSelectedListener,
        SongController.SongStateListener {

    private SongController songController;
    private PlayerCardView playerCardView;
    private NavBar navBar;
    private List<Singer> currentSingers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songController = new SongController(this);
        songController.setSongStateListener(this);

        navBar = findViewById(R.id.navbar);
        playerCardView = findViewById(R.id.playerCard);

        setupPlayerCardListeners();
        setupNavBar();

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), false);
        }
    }

    public List<Singer> getCurrentSingers() {
        return this.currentSingers;
    }

    public SongController getSongController() {
        return this.songController;
    }

    @Override
    public void onSongSelected(List<Song> songList, int position, List<Singer> singerList) {
        this.currentSingers = singerList;
        songController.setPlaylist(songList, position);
    }

    @Override
    public void onSongChanged(Song newSong) {
        playerCardView.setVisibility(View.VISIBLE);
        playerCardView.setSongTitle(newSong.getTitle());
        playerCardView.setAlbumImage(newSong.getImageUrl());

        String singerName = "Unknown Artist";
        for (Singer s : currentSingers) {
            if (s.getId() != null && s.getId().equals(newSong.getSingerId())) {
                singerName = s.getName();
                break;
            }
        }
        playerCardView.setSingerName(singerName);
    }

    @Override
    public void onPlaybackStateChanged(boolean isPlaying) {
        playerCardView.showAsPlaying(isPlaying);
    }

    @Override
    public void onProgressUpdate(int currentPosition, int maxDuration) {
        playerCardView.setMaxProgress(maxDuration);
        playerCardView.setProgress(currentPosition);
    }

    private void setupPlayerCardListeners() {
        playerCardView.setOnClickListener(v -> {
            showFullScreenPlayer(true);
            loadFragment(new PlayingScreen(), true);
        });

        playerCardView.setOnPauseClickListener(v -> {
            songController.pauseSong();
        });

        playerCardView.setOnPlayClickListener(v -> {
            songController.resumeSong();
        });

        playerCardView.setOnNextClickListener(v -> {
            songController.playNextSong();
        });
    }

    private void setupNavBar() {
        navBar.selectTab("home");
        navBar.setNavBarListener(tab -> {
            Fragment selectedFragment = null;
            switch (tab) {
                case "home":
                    selectedFragment = new HomeFragment();
                    break;
                case "capsuled":
                    selectedFragment = new CapsuleFragment();
                    break;
                case "profile":
                    selectedFragment = new Profile();
                    break;
            }
            if (selectedFragment != null) {
                loadFragment(selectedFragment, false);
            }
        });
    }

    public void showFullScreenPlayer(boolean show) {
        if (show) {
            navBar.setVisibility(View.GONE);
            playerCardView.setVisibility(View.GONE);
        } else {
            navBar.setVisibility(View.VISIBLE);
            // Player card hanya ditampilkan jika ada lagu yang sedang disiapkan
            if (songController.isPlaylistSet()) { // Anda perlu menambahkan metode isPlaylistSet() di SongController
                playerCardView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof PlayingScreen) {
            showFullScreenPlayer(false);
        }
        super.onBackPressed();
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        if (fragment != null) {
            var transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            if (addToBackStack) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        songController.release();
    }
}