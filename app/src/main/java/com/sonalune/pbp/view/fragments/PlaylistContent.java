package com.sonalune.pbp.view.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sonalune.pbp.R;
import com.sonalune.pbp.model.Singer;
import com.sonalune.pbp.model.Song;
import com.sonalune.pbp.view.adapters.SongAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlaylistContent extends Fragment {

    private RecyclerView recyclerViewSong;
    private FirebaseFirestore db;
    private ImageView imagePlaylistContent, backButton;
    private OnSongSelectedListener songSelectedListener;
    private List<Song> playlistSong = new ArrayList<>();
    private List<Singer> singerList = new ArrayList<>();
    private SongAdapter songAdapter;
    private String playlistId;

    public interface OnSongSelectedListener {
        void onSongSelected(List<Song> songList, int position, List<Singer> singerList);
    }

    public static PlaylistContent newInstance(String playlistId, String imageUrl) {
        PlaylistContent fragment = new PlaylistContent();
        Bundle args = new Bundle();
        args.putString("id", playlistId);
        args.putString("imageUrl", imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaylistContent() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSongSelectedListener) {
            songSelectedListener = (OnSongSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSongSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_content, container, false);
        initViews(view);
        initAdapter();
        setupListeners();
        loadPlaylistData();

        return view;
    }

    private void initViews(View view) {
        db = FirebaseFirestore.getInstance();
        recyclerViewSong = view.findViewById(R.id.recyclerViewSong);
        imagePlaylistContent = view.findViewById(R.id.img_playlist_content);
        backButton = view.findViewById(R.id.backButtonPlaylist);

        recyclerViewSong.setLayoutManager(new LinearLayoutManager(getContext()));

        backButton.setOnClickListener(v -> {
            if (isAdded()) requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void initAdapter() {
        // Inisialisasi variabel anggota kelas songAdapter
        songAdapter = new SongAdapter(playlistSong, singerList);
        recyclerViewSong.setAdapter(songAdapter);
    }

    private void setupListeners() {
        if (songAdapter == null) return;

        songAdapter.setOnItemClickListener(position -> {
            if (songSelectedListener != null) {
                songSelectedListener.onSongSelected(playlistSong, position, singerList);
            }
        });

        songAdapter.setOnMoreOptionsClickListener((view, song) -> {
            showPopupMenu(view, song);
        });
    }

    private void showPopupMenu(View view, Song song) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater().inflate(R.menu.song_option_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_remove_from_playlist) {
                removeSongFromPlaylist(song);
                return true;
            } else if (itemId == R.id.action_add_to_playlist) {
                Toast.makeText(getContext(), "Fitur 'Tambah ke playlist' belum diimplementasikan.", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.action_create_playlist) {
                Toast.makeText(getContext(), "Fitur 'Buat playlist' belum diimplementasikan.", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void removeSongFromPlaylist(Song song) {
        if (playlistId == null || song.getId() == null) {
            Toast.makeText(getContext(), "Error: Gagal mendapatkan ID", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference playlistRef = db.collection("Playlist").document(playlistId);
        playlistRef.update("songId", FieldValue.arrayRemove(song.getId()))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), song.getTitle() + " dihapus dari playlist.", Toast.LENGTH_SHORT).show();
                    int position = playlistSong.indexOf(song);
                    if (position != -1) {
                        playlistSong.remove(position);
                        songAdapter.notifyItemRemoved(position);
                        songAdapter.notifyItemRangeChanged(position, playlistSong.size());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal menghapus lagu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadPlaylistData() {
        this.playlistId = getArguments() != null ? getArguments().getString("id") : null;
        String imagePlaylistUrl = getArguments() != null ? getArguments().getString("imageUrl") : null;

        if (imagePlaylistUrl != null) {
            Glide.with(this).load(imagePlaylistUrl).into(imagePlaylistContent);
        }
        if (this.playlistId == null) return;

        db.collection("Playlist").document(playlistId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> songIds = (List<String>) documentSnapshot.get("songId");
                        if (songIds != null && !songIds.isEmpty()) {
                            int batchSize = 10;
                            playlistSong.clear();
                            Set<String> singerIds = new HashSet<>();
                            for (int i = 0; i < songIds.size(); i += batchSize) {
                                List<String> batch = songIds.subList(i, Math.min(i + batchSize, songIds.size()));
                                db.collection("Song")
                                        .whereIn("__name__", batch)
                                        .get()
                                        .addOnSuccessListener(querySnapshot -> {
                                            for (DocumentSnapshot doc : querySnapshot) {
                                                Song song = doc.toObject(Song.class);
                                                playlistSong.add(song);
                                                if (song.getSingerId() != null) {
                                                    singerIds.add(song.getSingerId());
                                                }
                                            }
                                            if (!singerIds.isEmpty()) {
                                                db.collection("Singer")
                                                        .whereIn("__name__", new ArrayList<>(singerIds))
                                                        .get()
                                                        .addOnSuccessListener(singerSnapshot -> {
                                                            singerList.clear();
                                                            for (DocumentSnapshot singerDoc : singerSnapshot) {
                                                                Singer singer = singerDoc.toObject(Singer.class);
                                                                singer.setId(singerDoc.getId());
                                                                singerList.add(singer);
                                                            }
                                                            songAdapter.notifyDataSetChanged();
                                                        });
                                            } else {
                                                songAdapter.notifyDataSetChanged();
                                            }
                                        });
                            }
                        }
                    }
                });

        songAdapter.setOnItemClickListener(position -> {
            if (songSelectedListener != null) {
                songSelectedListener.onSongSelected(playlistSong, position, singerList);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        songSelectedListener = null;
    }
}