package com.sonalune.pbp.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sonalune.pbp.R;
import com.sonalune.pbp.model.Playlist;
import com.sonalune.pbp.model.Singer;
import com.sonalune.pbp.model.Song;
import com.sonalune.pbp.view.activities.MainActivity;
import com.sonalune.pbp.view.adapters.PickForYouAdapter;
import com.sonalune.pbp.view.adapters.PlaylistAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerViewPlaylist, recyclerViewPickForYou;
    private FirebaseFirestore db;
    private String currentUserId;
    private MainActivity mainActivity;

    public HomeFragment() {
        // Required empty public constructor
    }

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
        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";

        setupPlaylistRecyclerView(view);
        setupPickForYouRecyclerView(view);

        return view;
    }

    private void setupPlaylistRecyclerView(View view) {
        recyclerViewPlaylist = view.findViewById(R.id.recyclerViewPlaylist);
        recyclerViewPlaylist.setLayoutManager(new GridLayoutManager(getContext(), 2));

        List<Playlist> allPlaylist = new ArrayList<>();
        PlaylistAdapter adapterPlaylist = new PlaylistAdapter(allPlaylist);
        recyclerViewPlaylist.setAdapter(adapterPlaylist);

        db.collection("Playlist")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allPlaylist.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Playlist playlist = doc.toObject(Playlist.class);
                        playlist.setId(doc.getId());
                        if (playlist.getIsPublic() || playlist.getUserId().equals(currentUserId)) {
                            allPlaylist.add(playlist);
                        }
                    }
                    adapterPlaylist.notifyDataSetChanged();
                });

        adapterPlaylist.setOnItemClickListener(playlist -> {
            PlaylistContent fragment = PlaylistContent.newInstance(playlist.getId(), playlist.getImageUrl());
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

        db.collection("Playlist").document(pickForYouPlaylistId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> allSongIds = (List<String>) documentSnapshot.get("songId");
                        if (allSongIds != null && !allSongIds.isEmpty()) {
                            List<String> top6SongIds = allSongIds.subList(0, Math.min(allSongIds.size(), 6));
                            if (top6SongIds.isEmpty()) return;

                            db.collection("Song").whereIn("__name__", top6SongIds)
                                    .get()
                                    .addOnSuccessListener(songQuerySnapshot -> {
                                        pickForYouSongs.clear();
                                        Set<String> singerIds = new HashSet<>();
                                        for (DocumentSnapshot songDoc : songQuerySnapshot) {
                                            Song song = songDoc.toObject(Song.class);
                                            song.setId(songDoc.getId());
                                            pickForYouSongs.add(song);
                                            if (song.getSingerId() != null && !song.getSingerId().isEmpty()) {
                                                singerIds.add(song.getSingerId());
                                            }
                                        }

                                        if (!singerIds.isEmpty()) {
                                            db.collection("Singer").whereIn("__name__", new ArrayList<>(singerIds))
                                                    .get()
                                                    .addOnSuccessListener(singerQuerySnapshot -> {
                                                        pickForYouSingers.clear();
                                                        for(DocumentSnapshot singerDoc : singerQuerySnapshot) {
                                                            Singer singer = singerDoc.toObject(Singer.class);
                                                            singer.setId(singerDoc.getId());
                                                            pickForYouSingers.add(singer);
                                                        }
                                                       adapterPickForYou.notifyDataSetChanged();
                                                    });
                                        } else {
                                            adapterPickForYou.notifyDataSetChanged();
                                        }
                                    });
                        }
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