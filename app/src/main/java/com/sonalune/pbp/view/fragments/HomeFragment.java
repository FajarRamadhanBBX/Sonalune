package com.sonalune.pbp.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.sonalune.pbp.R;
import com.sonalune.pbp.controller.HomeController;
import com.sonalune.pbp.model.Playlist;
import com.sonalune.pbp.model.Singer;
import com.sonalune.pbp.model.Song;
import com.sonalune.pbp.view.activities.MainActivity;
import com.sonalune.pbp.view.adapters.PickForYouAdapter;
import com.sonalune.pbp.view.adapters.PlaylistAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerViewPlaylist, recyclerViewPickForYou;
    private String currentUserId;
    private MainActivity mainActivity;
    private HomeController homeController;
    private EditText etSearch;

    public HomeFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mainActivity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        homeController = new HomeController();

        setupPlaylistRecyclerView(view);
        setupPickForYouRecyclerView(view);
        loadUserProfilePhoto(view);
        etSearch = view.findViewById(R.id.etSearch); // Inisialisasi EditText
        setupSearch();

        return view;
    }

    private void setupSearch() {
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = etSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    // Pindah ke SearchResultFragment
                    SearchResultFragment fragment = SearchResultFragment.newInstance(query);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
                return true;
            }
            return false;
        });
    }

    private void loadUserProfilePhoto(View view) {
        CircleImageView profileImageView = view.findViewById(R.id.photoProfile);
        if (currentUserId != null && !currentUserId.isEmpty()) {
            homeController.loadUserPhoto(currentUserId, new HomeController.UserPhotoListener() {
                @Override
                public void onPhotoLoaded(String photoUrl) {
                    if (isAdded() && photoUrl != null && !photoUrl.isEmpty()) {
                        Glide.with(getContext())
                                .load(photoUrl)
                                .placeholder(R.drawable.im_antony)
                                .error(R.drawable.im_antony)
                                .into(profileImageView);
                    }
                }
                @Override
                public void onError() {
                    if (isAdded()) {
                        profileImageView.setImageResource(R.drawable.im_antony);
                    }
                }
            });
        }
    }

    private void setupPlaylistRecyclerView(View view) {
        recyclerViewPlaylist = view.findViewById(R.id.recyclerViewPlaylist);
        recyclerViewPlaylist.setLayoutManager(new GridLayoutManager(getContext(), 2));

        List<Playlist> allPlaylist = new ArrayList<>();
        PlaylistAdapter adapterPlaylist = new PlaylistAdapter(allPlaylist);
        recyclerViewPlaylist.setAdapter(adapterPlaylist);

        homeController.loadPlaylists(currentUserId, new HomeController.PlaylistListener() {
            @Override
            public void onPlaylistLoaded(List<Playlist> playlists) {
                allPlaylist.clear();
                allPlaylist.addAll(playlists);
                adapterPlaylist.notifyDataSetChanged();
            }
            @Override
            public void onError(String message) {
                // Optional: tampilkan pesan error
            }
        });

        adapterPlaylist.setOnItemClickListener(playlist -> {
            PlaylistContent fragment = PlaylistContent.newInstance(playlist.getId(), playlist.getImageUrl(), playlist.getIsPublic());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setupPickForYouRecyclerView(View view) {
        recyclerViewPickForYou = view.findViewById(R.id.recyclerViewPickForYou);
        recyclerViewPickForYou.setLayoutManager(new GridLayoutManager(getContext(), 3));

        List<Song> pickForYouSongs = new ArrayList<>();
        List<Singer> pickForYouSingers = new ArrayList<>();
        PickForYouAdapter adapterPickForYou = new PickForYouAdapter(pickForYouSongs);
        recyclerViewPickForYou.setAdapter(adapterPickForYou);

        String pickForYouPlaylistId = "DDApc9gdv0JRbBL4Qd97";

        homeController.loadPickForYou(pickForYouPlaylistId, new HomeController.PickForYouListener() {
            @Override
            public void onPickForYouLoaded(List<Song> songs, List<Singer> singers) {
                pickForYouSongs.clear();
                pickForYouSongs.addAll(songs);
                pickForYouSingers.clear();
                pickForYouSingers.addAll(singers);
                adapterPickForYou.notifyDataSetChanged();
            }
            @Override
            public void onError(String message) {
                // Optional: tampilkan pesan error
            }
        });

        adapterPickForYou.setOnItemClickListener(song -> {
            if (mainActivity != null) {
                List<Song> singleSongPlaylist = new ArrayList<>(Collections.singletonList(song));
                mainActivity.onSongSelected(singleSongPlaylist, 0, pickForYouSingers);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }
}