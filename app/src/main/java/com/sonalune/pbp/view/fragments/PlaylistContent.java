package com.sonalune.pbp.view.fragments;

import android.os.Bundle;
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
import com.sonalune.pbp.controller.SongController;
import com.sonalune.pbp.model.Singer;
import com.sonalune.pbp.model.Song;
import com.sonalune.pbp.view.adapters.SongAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaylistContent#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class PlaylistContent extends Fragment {

    private RecyclerView recyclerViewSong;
    private FirebaseFirestore db;
    private ImageView imagePlaylistContent;
    private SongController songController;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_content, container, false);
        ImageView backButton = view.findViewById(R.id.btn_back);
        db = FirebaseFirestore.getInstance();
        imagePlaylistContent = view.findViewById(R.id.img_playlist_content);
        recyclerViewSong = view.findViewById(R.id.recyclerViewSong);
        recyclerViewSong.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set an OnClickListener for the back button
        backButton.setOnClickListener(v -> {
            // Navigate back to the previous fragment
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        List<Song> playlistSong = new ArrayList<>();
        List<Singer> singerList = new ArrayList<>();
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

        songController = new SongController(requireContext());
//        playerCard = new PlayerCard(requireContext(), null);

        songAdapter.setOnItemClickListener(song -> {
            String songUrl = song.getSongUrl();
            Singer singer = null;
            for (Singer s : singerList) {
                if (s.getId() != null && s.getId().equals(song.getSingerId())) {
                    singer = s;
                    break;
                }
            }
            if (songUrl != null && !songUrl.isEmpty()){
                songController.playSong(songUrl);
//                playerCard.setVisibility(View.VISIBLE);
//                playerCard.setSongInfo(song, singer);
            } else {
                Toast.makeText(getContext(), "URL tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }}