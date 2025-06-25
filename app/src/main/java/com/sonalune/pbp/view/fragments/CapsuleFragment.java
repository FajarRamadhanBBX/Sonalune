package com.sonalune.pbp.view.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sonalune.pbp.R;
import com.sonalune.pbp.controller.HistoryController;
import com.sonalune.pbp.model.Singer;
import com.sonalune.pbp.model.Song;
import com.sonalune.pbp.model.User;
import com.sonalune.pbp.view.adapters.CapsuleSectionAdapter;

import java.util.ArrayList;
import java.util.List;

public class CapsuleFragment extends Fragment {

    private RecyclerView rvCapsuleMain;
    private CapsuleSectionAdapter adapter;
    private HistoryController historyController;

    public CapsuleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_capsule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        historyController = new HistoryController();
        rvCapsuleMain = view.findViewById(R.id.rv_capsule_main);

        setupRecyclerView();
        loadCapsuleData();
    }

    private void setupRecyclerView() {
        adapter = new CapsuleSectionAdapter(getContext());
        rvCapsuleMain.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCapsuleMain.setAdapter(adapter);
    }

    private void loadCapsuleData() {
        historyController.getCapsuleData(new HistoryController.CapsuleDataListener() {
            @Override
            public void onDataLoaded(User user, List<Song> topSongsAllTime, List<Singer> topArtistsAllTime, List<Song> topSongsMonthly, List<Singer> topArtistsMonthly) {
                if (!isAdded()) return; // Pastikan fragment masih aktif

                adapter.setData(user, topSongsAllTime, topArtistsAllTime, topSongsMonthly, topArtistsMonthly);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}