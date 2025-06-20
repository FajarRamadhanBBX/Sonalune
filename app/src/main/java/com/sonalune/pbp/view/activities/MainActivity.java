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

// DITAMBAHKAN: Implementasikan interface dari fragment
public class MainActivity extends AppCompatActivity implements PlaylistContent.OnSongSelectedListener {

    private SongController songController;
    private PlayerCardView playerCardView;
    private NavBar navBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi Controller dan Views
        songController = new SongController(this);
        navBar = findViewById(R.id.navbar);
        playerCardView = findViewById(R.id.playerCard);

        // DIHAPUS: Blok kode ini salah secara konseptual karena membuat instance fragment
        // yang tidak pernah digunakan. Logika ini akan dipindahkan ke metode onSongSelected().
        /*
        PlaylistContent playlistContent = new PlaylistContent();
        playlistContent.setOnSongSelectedListener((song, singer, songUrl) -> { ... });
        */

        // Atur listener untuk komponen UI
        setupPlayerCardListeners();
        setupNavBar();

        // Load fragment awal
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), false); // Jangan tambahkan fragment awal ke back stack
        }
    }

    // DITAMBAHKAN: Implementasi dari metode interface.
    // Metode ini akan dipanggil oleh PlaylistContentFragment ketika lagu dipilih.
    @Override
    public void onSongSelected(Song song, Singer singer) {
        playerCardView.setVisibility(View.VISIBLE);
        playerCardView.setSongTitle(song.getTitle());
        if (singer != null) {
            playerCardView.setSingerName(singer.getName());
        } else {
            playerCardView.setSingerName("Unknown Artist");
        }

        playerCardView.setAlbumImage(song.getImageUrl());

        // Perintahkan controller untuk memutar lagu
        songController.playSong(song.getSongUrl());
        // Perintahkan view untuk menampilkan state "sedang bermain"
        playerCardView.showAsPlaying(true);
    }

    private void setupPlayerCardListeners() {
        // Listener untuk membuka layar penuh (PlayingScreen)
        playerCardView.setOnClickListener(v -> {
            loadFragment(new PlayingScreen(), true);
        });

        // Listener untuk tombol pause
        playerCardView.setOnPauseClickListener(v -> {
            songController.pauseSong();
            playerCardView.showAsPlaying(false); // Update UI ke state "dijeda"
        });

        // Listener untuk tombol play
        playerCardView.setOnPlayClickListener(v -> {
            songController.resumeSong(); // Anda perlu membuat method ini di SongController
            playerCardView.showAsPlaying(true); // Update UI ke state "bermain"
        });

        playerCardView.setOnNextClickListener(v -> {
            // TODO: Implementasikan logika untuk lagu selanjutnya
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
                // Jangan tambahkan fragment utama ke back stack
                loadFragment(selectedFragment, false);
            }
        });
    }

    // DIUBAH: Menjadi public dan ditambahkan parameter untuk mengontrol back stack
    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        if (fragment != null) {
            var transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            if (addToBackStack) {
                transaction.addToBackStack(null); // Agar tombol back berfungsi
            }
            transaction.commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Selalu lepaskan resource untuk mencegah memory leak
        songController.release();
    }
}