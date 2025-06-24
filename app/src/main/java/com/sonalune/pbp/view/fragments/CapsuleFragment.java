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

    // Deklarasi untuk komponen UI dan Controller
    private RecyclerView rvCapsuleMain;
    private CapsuleSectionAdapter adapter;
    private HistoryController historyController;

    public CapsuleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout baru yang sudah kita perbaiki
        return inflater.inflate(R.layout.fragment_capsule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi controller
        historyController = new HistoryController();
        // Temukan RecyclerView dari layout
        rvCapsuleMain = view.findViewById(R.id.rv_capsule_main);

        // Panggil metode untuk setup RecyclerView dan memuat data
        setupRecyclerView();
        loadCapsuleData();
    }

    /**
     * Metode untuk menyiapkan RecyclerView dan Adapternya.
     */
    private void setupRecyclerView() {
        // Buat instance dari adapter utama kita
        adapter = new CapsuleSectionAdapter(getContext());
        // Atur layout manager
        rvCapsuleMain.setLayoutManager(new LinearLayoutManager(getContext()));
        // Pasang adapter ke RecyclerView
        rvCapsuleMain.setAdapter(adapter);
    }

    /**
     * Metode untuk memanggil HistoryController dan memuat data dari Firestore.
     */
    private void loadCapsuleData() {
        // Tampilkan loading indicator jika ada

        historyController.getCapsuleData(new HistoryController.CapsuleDataListener() {
            @Override
            public void onDataLoaded(User user, List<Song> topSongs, List<Singer> topArtists) {
                // Pastikan fragment masih ada sebelum update UI
                if (!isAdded()) return;

                // Kirim semua data yang didapat dari controller ke adapter
                adapter.setData(user, topSongs, topArtists);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}