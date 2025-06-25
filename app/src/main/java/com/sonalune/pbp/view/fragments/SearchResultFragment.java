package com.sonalune.pbp.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

public class SearchResultFragment extends Fragment {

    private static final String ARG_QUERY = "search_query";
    private String searchQuery;

    private RecyclerView rvSearchResults;
    private TextView tvSearchQuery;
    private ImageView btnBack;
    private SongAdapter songAdapter;
    private List<Song> songResults = new ArrayList<>();
    private HomeController homeController;
    private PlaylistController playlistController;
    private MainActivity mainActivity;

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
        homeController = new HomeController();

        tvSearchQuery.setText("Hasil untuk: \"" + searchQuery + "\"");
        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        setupRecyclerView();
        executeSearch();
    }

    private void setupRecyclerView() {
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSearchResults.setAdapter(songAdapter);

        songAdapter.setOnItemClickListener(position -> {
            if(mainActivity != null) {
                mainActivity.getSongController().setPlaylist(new ArrayList<>(Collections.singletonList(songResults.get(position))), 0);
            }
        });
    }

    private void executeSearch() {
        if (searchQuery == null || searchQuery.trim().isEmpty()) return;

        homeController.searchSongs(searchQuery.toLowerCase(), new HomeController.SearchResultListener() {
            @Override
            public void onSearchResult(List<Song> songs, List<Singer> singers) {
                if (!isAdded()) return;
                songResults.clear();
                songResults.addAll(songs);
                songAdapter = new SongAdapter(songResults, singers, false);
                rvSearchResults.setAdapter(songAdapter);
                songAdapter.notifyDataSetChanged();
                if (songs.isEmpty()){
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

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }
}