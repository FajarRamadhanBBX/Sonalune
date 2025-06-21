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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private ImageView imagePlaylistContent;
    private OnSongSelectedListener songSelectedListener;
    private List<Song> playlistSong = new ArrayList<>();
    private List<Singer> singerList = new ArrayList<>();

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
            // Melempar error jika Activity lupa mengimplementasikan listener, ini mencegah bug
            throw new RuntimeException(context.toString()
                    + " must implement OnSongSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_content, container, false);
        ImageView backButton = view.findViewById(R.id.btn_back);
        db = FirebaseFirestore.getInstance();
        imagePlaylistContent = view.findViewById(R.id.img_playlist_content);
        recyclerViewSong = view.findViewById(R.id.recyclerViewSong);
        recyclerViewSong.setLayoutManager(new LinearLayoutManager(getContext()));

        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });


        SongAdapter songAdapter = new SongAdapter(playlistSong, singerList);
        recyclerViewSong.setAdapter(songAdapter);

        String playlistId = getArguments() != null ? getArguments().getString("id") : null;
        String imagePlaylistUrl = getArguments() != null ? getArguments().getString("imageUrl") : null;
        if (imagePlaylistUrl != null) {
            Glide.with(this).load(imagePlaylistUrl).into(imagePlaylistContent);
        }
        if (playlistId == null) return view;

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

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        songSelectedListener = null;
    }
}