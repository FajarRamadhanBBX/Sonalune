package com.sonalune.pbp.view.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sonalune.pbp.R;
import com.sonalune.pbp.model.Playlist;
import com.sonalune.pbp.view.adapters.dialog.PlaylistSelectionAdapter;
import java.util.ArrayList;
import java.util.List;

public class AddToPlaylistDialogFragment extends DialogFragment {

    private RecyclerView rvPlaylists;
    private Button btnCancelDialog;
    private PlaylistSelectionAdapter adapter;
    private List<Playlist> userPlaylists = new ArrayList<>();
    private FirebaseFirestore db;
    private String currentUserId;
    private String excludePlaylistId; // ID playlist saat ini untuk dikecualikan dari daftar

    // Interface untuk berkomunikasi kembali ke PlaylistContent
    public interface OnPlaylistSelectedListener {
        void onPlaylistSelected(String playlistId);
    }

    private OnPlaylistSelectedListener listener;

    public static AddToPlaylistDialogFragment newInstance(String excludePlaylistId, OnPlaylistSelectedListener listener) {
        AddToPlaylistDialogFragment fragment = new AddToPlaylistDialogFragment();
        fragment.excludePlaylistId = excludePlaylistId;
        fragment.listener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout untuk dialog ini
        View view = inflater.inflate(R.layout.fragment_add_to_playlist_dialog, container, false);

        // Inisialisasi Firebase
        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        // Hubungkan view dari layout
        rvPlaylists = view.findViewById(R.id.rvPlaylists);
        btnCancelDialog = view.findViewById(R.id.btnCancelDialog);

        setupRecyclerView();
        loadUserPlaylists();

        btnCancelDialog.setOnClickListener(v -> dismiss());

        return view;
    }

    private void setupRecyclerView() {
        // Buat adapter baru. Saat sebuah playlist di adapter diklik, panggil listener dan tutup dialog.
        adapter = new PlaylistSelectionAdapter(userPlaylists, playlist -> {
            if (listener != null) {
                listener.onPlaylistSelected(playlist.getId());
            }
            dismiss();
        });
        rvPlaylists.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPlaylists.setAdapter(adapter);
    }

    private void loadUserPlaylists() {
        if (currentUserId == null) {
            Toast.makeText(getContext(), "Anda harus login terlebih dahulu", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        // Query ke Firestore untuk mengambil semua playlist milik pengguna saat ini
        db.collection("Playlist")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!isAdded()) return; // Pastikan fragment masih ter-attach
                    userPlaylists.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // Logika untuk mengecualikan playlist yang sedang dibuka
                        if (!doc.getId().equals(excludePlaylistId)) {
                            Playlist playlist = doc.toObject(Playlist.class);
                            playlist.setId(doc.getId());
                            userPlaylists.add(playlist);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Gagal memuat playlist: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}