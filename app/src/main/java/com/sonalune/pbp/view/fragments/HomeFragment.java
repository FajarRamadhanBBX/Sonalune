package com.sonalune.pbp.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sonalune.pbp.R;
import com.sonalune.pbp.model.Playlist;
import com.sonalune.pbp.model.Song;
import com.sonalune.pbp.view.adapters.PickForYouAdapter;
import com.sonalune.pbp.view.adapters.PlaylistAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerViewPlaylist, recyclerViewPickForYou;
    private FirebaseFirestore db;
    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        db = FirebaseFirestore.getInstance();

        // Playlist
        recyclerViewPlaylist = view.findViewById(R.id.recyclerViewPlaylist);
        recyclerViewPlaylist.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Buat daftar playlist
        List<Playlist> allPlaylist = new ArrayList<>();
        PlaylistAdapter adapterPlaylist = new PlaylistAdapter(allPlaylist);
        recyclerViewPlaylist.setAdapter(adapterPlaylist);

        db.collection("Playlist")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allPlaylist.clear();
                    for (DocumentSnapshot doc: queryDocumentSnapshots) {
                        Playlist playlist = doc.toObject(Playlist.class);
                        if (playlist.getIsPublic() || playlist.getUserId().equals(currentUserId))
                            allPlaylist.add(playlist);
                    }
                    adapterPlaylist.notifyDataSetChanged();
                });

        adapterPlaylist.setOnItemClickListener(playlist -> {
            String playlistId = playlist.getId();
            String playlistimage = playlist.getImageUrl();
            Fragment fragment = PlaylistContent.newInstance(playlistId, playlistimage);
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Pick For You
        recyclerViewPickForYou = view.findViewById(R.id.recyclerViewPickForYou);
        recyclerViewPickForYou.setLayoutManager(new GridLayoutManager(getContext(), 3));

        // Buat daftar lagu
        List<Song> allSong = new ArrayList<Song>();
//        allSong.add(new Song("id", "title", "singerId", 0 ,"https://bzvdoaouvekmijrdgmbz.supabase.co/storage/v1/object/sign/uasplatform/playlist/doves.jpeg?token=eyJraWQiOiJzdG9yYWdlLXVybC1zaWduaW5nLWtleV9mYTNiMGUwZS0wZTMxLTQyOTEtOWJmYS0zNjk5MmQ0ZGM5ZDMiLCJhbGciOiJIUzI1NiJ9.eyJ1cmwiOiJ1YXNwbGF0Zm9ybS9wbGF5bGlzdC9kb3Zlcy5qcGVnIiwiaWF0IjoxNzUwNDI0NjY1LCJleHAiOjE3ODE5NjA2NjV9.jEQjzz9aSJDIE5Aql8jkYbUQdhZwoyvW29DHPFlQFwE"));

        // Pasang adapter
        PickForYouAdapter adapterPickForYou = new PickForYouAdapter(allSong);
        recyclerViewPickForYou.setAdapter(adapterPickForYou);

        return view;
    }
}