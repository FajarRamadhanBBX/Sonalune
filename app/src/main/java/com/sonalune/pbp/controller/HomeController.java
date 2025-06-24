package com.sonalune.pbp.controller;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sonalune.pbp.model.Playlist;
import com.sonalune.pbp.model.Singer;
import com.sonalune.pbp.model.Song;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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