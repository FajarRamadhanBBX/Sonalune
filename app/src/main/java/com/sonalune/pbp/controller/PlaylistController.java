package com.sonalune.pbp.controller;

import android.content.Context;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.sonalune.pbp.model.Playlist;
import com.sonalune.pbp.model.Singer;
import com.sonalune.pbp.model.Song;

import java.util.*;

public class PlaylistController {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface PlaylistDataListener {
        void onPlaylistLoaded(List<Song> songs, List<Singer> singers, String imageUrl);
        void onError(String message);
    }

    public interface SimpleListener {
        void onSuccess(String message);
        void onFailure(String message);
    }

    public void loadPlaylist(String playlistId, String imageUrl, PlaylistDataListener listener) {
        db.collection("Playlist").document(playlistId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> songIds = (List<String>) documentSnapshot.get("songId");
                        if (songIds != null && !songIds.isEmpty()) {
                            int batchSize = 10;
                            List<Song> playlistSong = new ArrayList<>();
                            Set<String> singerIds = new HashSet<>();
                            List<String> songIdsCopy = new ArrayList<>(songIds);
                            for (int i = 0; i < songIdsCopy.size(); i += batchSize) {
                                List<String> batch = songIdsCopy.subList(i, Math.min(i + batchSize, songIdsCopy.size()));
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
                                                            List<Singer> singerList = new ArrayList<>();
                                                            for (DocumentSnapshot singerDoc : singerSnapshot) {
                                                                Singer singer = singerDoc.toObject(Singer.class);
                                                                singer.setId(singerDoc.getId());
                                                                singerList.add(singer);
                                                            }
                                                            listener.onPlaylistLoaded(playlistSong, singerList, imageUrl);
                                                        });
                                            } else {
                                                listener.onPlaylistLoaded(playlistSong, new ArrayList<>(), imageUrl);
                                            }
                                        });
                            }
                        } else {
                            listener.onPlaylistLoaded(new ArrayList<>(), new ArrayList<>(), imageUrl);
                        }
                    } else {
                        listener.onError("Playlist not found.");
                    }
                })
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    public void addSongToPlaylist(String playlistId, Song song, SimpleListener listener) {
        DocumentReference playlistRef = db.collection("Playlist").document(playlistId);
        playlistRef.update("songId", FieldValue.arrayUnion(song.getId()))
                .addOnSuccessListener(aVoid -> listener.onSuccess("Lagu ditambahkan ke playlist."))
                .addOnFailureListener(e -> listener.onFailure("Gagal menambahkan lagu."));
    }

    public void removeSongFromPlaylist(String playlistId, Song song, SimpleListener listener) {
        DocumentReference playlistRef = db.collection("Playlist").document(playlistId);
        playlistRef.update("songId", FieldValue.arrayRemove(song.getId()))
                .addOnSuccessListener(aVoid -> listener.onSuccess("Lagu dihapus dari playlist."))
                .addOnFailureListener(e -> listener.onFailure("Gagal menghapus lagu: " + e.getMessage()));
    }

    public void createPlaylist(String userId, String playlistName, Song song, SimpleListener listener) {
        Playlist newPlaylist = new Playlist();
        newPlaylist.setIsPublic(false);
        newPlaylist.setName(playlistName);
        newPlaylist.setUserId(userId);
        newPlaylist.setImageUrl(song.getImageUrl());
        newPlaylist.setSongId(new ArrayList<>(Collections.singletonList(song.getId())));
        db.collection("Playlist").add(newPlaylist)
                .addOnSuccessListener(documentReference -> listener.onSuccess("Playlist '" + playlistName + "' berhasil dibuat."))
                .addOnFailureListener(e -> listener.onFailure("Gagal membuat playlist."));
    }
}