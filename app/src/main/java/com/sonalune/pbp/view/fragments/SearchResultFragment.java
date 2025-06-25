package com.sonalune.pbp.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.sonalune.pbp.R;
import com.sonalune.pbp.controller.HomeController;
import com.sonalune.pbp.controller.PlaylistController;
import com.sonalune.pbp.model.Singer;
import com.sonalune.pbp.model.Song;
import com.sonalune.pbp.view.activities.MainActivity;
import com.sonalune.pbp.view.adapters.SongAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchResultFragment extends Fragment implements CreatePlaylistDialogFragment.OnPlaylistCreatedListener {

    private static final String ARG_QUERY = "search_query";
    private String searchQuery;

    private RecyclerView rvSearchResults;
    private TextView tvSearchQuery;
    private ImageView btnBack;
    private SongAdapter songAdapter;

    private List<Song> songResults = new ArrayList<>();
    private List<Singer> singerResults = new ArrayList<>();
    private HomeController homeController;
    private PlaylistController playlistController;
    private MainActivity mainActivity;
    private Song songForAction;

    public static SearchResultFragment newInstance(String query) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mainActivity = (MainActivity) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchQuery = getArguments().getString(ARG_QUERY);
        }
        homeController = new HomeController();
        playlistController = new PlaylistController();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvSearchResults = view.findViewById(R.id.rvResultSearch);
        tvSearchQuery = view.findViewById(R.id.textResultSearch);
        btnBack = view.findViewById(R.id.btnBackSearch);

        tvSearchQuery.setText("Hasil untuk: \"" + searchQuery + "\"");
        btnBack.setOnClickListener(v -> {
            if (isAdded()) requireActivity().getSupportFragmentManager().popBackStack();
        });

        setupRecyclerView();
        executeSearch();
    }

    private void setupRecyclerView() {
        songAdapter = new SongAdapter(songResults, singerResults, false);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSearchResults.setAdapter(songAdapter);
        attachAdapterListeners();
    }

    private void executeSearch() {
        if (searchQuery == null || searchQuery.trim().isEmpty()) return;

        homeController.searchSongs(searchQuery.toLowerCase(), new HomeController.SearchResultListener() {
            @Override
            public void onSearchResult(List<Song> songs, List<Singer> singers) {
                if (!isAdded()) return;

                songResults.clear();
                songResults.addAll(songs);
                singerResults.clear();
                singerResults.addAll(singers);
                songAdapter.notifyDataSetChanged();

                if (songs.isEmpty()) {
                    Toast.makeText(getContext(), "Tidak ada hasil ditemukan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void attachAdapterListeners() {
        songAdapter.setOnItemClickListener(position -> {
            if (mainActivity != null && songResults.size() > position) {
                mainActivity.getSongController().setPlaylist(
                        new ArrayList<>(Collections.singletonList(songResults.get(position))), 0
                );
            }
        });

        songAdapter.setOnMoreOptionsClickListener((view, song) -> {
            showPopupMenu(view, song);
        });
    }

    private void showPopupMenu(View view, Song song) {
        this.songForAction = song;
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater().inflate(R.menu.song_option_menu, popup.getMenu());

        MenuItem removeItem = popup.getMenu().findItem(R.id.action_remove_from_playlist);
        if (removeItem != null) {
            removeItem.setVisible(false);
        }

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_add_to_playlist) {
                showAddToPlaylistDialog();
                return true;
            } else if (itemId == R.id.action_create_playlist) {
                showCreatePlaylistDialog();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showCreatePlaylistDialog() {
        CreatePlaylistDialogFragment dialog = CreatePlaylistDialogFragment.newInstance(this);
        dialog.show(getParentFragmentManager(), "CreatePlaylistDialog");
    }

    private void showAddToPlaylistDialog() {
        AddToPlaylistDialogFragment dialog = AddToPlaylistDialogFragment.newInstance(null, selectedPlaylistId -> {
            addSongToPlaylist(selectedPlaylistId, songForAction);
        });
        dialog.show(getParentFragmentManager(), "AddToPlaylistDialog");
    }

    @Override
    public void onPlaylistCreated(String playlistName) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId == null || songForAction == null) return;
        playlistController.createPlaylist(userId, playlistName, songForAction, new PlaylistController.SimpleListener() {
            @Override public void onSuccess(String message) { Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show(); }
            @Override public void onFailure(String message) { Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show(); }
        });
    }

    private void addSongToPlaylist(String targetPlaylistId, Song song) {
        if (targetPlaylistId == null || song == null) return;
        playlistController.addSongToPlaylist(targetPlaylistId, song, new PlaylistController.SimpleListener() {
            @Override public void onSuccess(String message) { Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show(); }
            @Override public void onFailure(String message) { Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show(); }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }
}