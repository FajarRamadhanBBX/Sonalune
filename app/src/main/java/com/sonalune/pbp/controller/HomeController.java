package com.sonalune.pbp.controller;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sonalune.pbp.model.Playlist;
import com.sonalune.pbp.model.Singer;
import com.sonalune.pbp.model.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeController {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface PlaylistListener {
        void onPlaylistLoaded(List<Playlist> playlists);
        void onError(String message);
    }

    public interface PickForYouListener {
        void onPickForYouLoaded(List<Song> songs, List<Singer> singers);
        void onError(String message);
    }

    public interface UserPhotoListener {
        void onPhotoLoaded(String photoUrl);
        void onError();
    }
    public interface SearchResultListener {
        void onSearchResult(List<Song> songs, List<Singer> singers);
        void onError(String message);
    }

    public void searchSongs(String query, SearchResultListener listener) {
        String searchQuery = query.toLowerCase();

        // Langkah 1: Cari lagu yang judulnya cocok (prefix search)
        db.collection("Song")
                .orderBy("title") // Pastikan Anda punya field ini dan sudah membuat index
                .startAt(searchQuery)
                .endAt(searchQuery + '\uf8ff')
                .get()
                .addOnSuccessListener(songQuerySnapshot -> {
                    List<Song> foundSongs = new ArrayList<>();
                    Set<String> singerIds = new HashSet<>();

                    for (DocumentSnapshot doc : songQuerySnapshot) {
                        Song song = doc.toObject(Song.class);
                        if (song != null) {
                            song.setId(doc.getId());
                            foundSongs.add(song);
                            if (song.getSingerId() != null) {
                                singerIds.add(song.getSingerId());
                            }
                        }
                    }

                    if (foundSongs.isEmpty()) {
                        listener.onSearchResult(new ArrayList<>(), new ArrayList<>());
                        return;
                    }

                    // Langkah 2: Ambil detail penyanyi dari lagu-lagu yang ditemukan
                    db.collection("Singer").whereIn(FieldPath.documentId(), new ArrayList<>(singerIds))
                            .get()
                            .addOnSuccessListener(singerQuerySnapshot -> {
                                List<Singer> foundSingers = new ArrayList<>();
                                for (DocumentSnapshot doc : singerQuerySnapshot) {
                                    Singer singer = doc.toObject(Singer.class);
                                    if (singer != null) {
                                        singer.setId(doc.getId());
                                        foundSingers.add(singer);
                                    }
                                }
                                listener.onSearchResult(foundSongs, foundSingers);
                            })
                            .addOnFailureListener(e -> listener.onError(e.getMessage()));
                })
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    public void loadPlaylists(String currentUserId, PlaylistListener listener) {
        db.collection("Playlist").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Playlist> allPlaylist = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Playlist playlist = doc.toObject(Playlist.class);
                        playlist.setId(doc.getId());
                        if (playlist.getIsPublic() || playlist.getUserId().equals(currentUserId)) {
                            allPlaylist.add(playlist);
                        }
                    }
                    listener.onPlaylistLoaded(allPlaylist);
                })
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    public void loadPickForYou(String playlistId, PickForYouListener listener) {
        db.collection("Playlist").document(playlistId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> allSongIds = (List<String>) documentSnapshot.get("songId");
                        if (allSongIds != null && !allSongIds.isEmpty()) {
                            List<String> top6SongIds = allSongIds.subList(0, Math.min(allSongIds.size(), 6));
                            db.collection("Song").whereIn("__name__", top6SongIds)
                                    .get()
                                    .addOnSuccessListener(songQuerySnapshot -> {
                                        List<Song> pickForYouSongs = new ArrayList<>();
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
                                                        List<Singer> pickForYouSingers = new ArrayList<>();
                                                        for (DocumentSnapshot singerDoc : singerQuerySnapshot) {
                                                            Singer singer = singerDoc.toObject(Singer.class);
                                                            singer.setId(singerDoc.getId());
                                                            pickForYouSingers.add(singer);
                                                        }
                                                        listener.onPickForYouLoaded(pickForYouSongs, pickForYouSingers);
                                                    });
                                        } else {
                                            listener.onPickForYouLoaded(pickForYouSongs, new ArrayList<>());
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    public void loadUserPhoto(String userId, UserPhotoListener listener) {
        db.collection("User").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String photoUrl = documentSnapshot.getString("photo");
                        listener.onPhotoLoaded(photoUrl);
                    } else {
                        listener.onError();
                    }
                })
                .addOnFailureListener(e -> listener.onError());
    }
}