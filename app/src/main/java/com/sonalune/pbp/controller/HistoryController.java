package com.sonalune.pbp.controller;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sonalune.pbp.model.History;
import com.sonalune.pbp.model.Singer;
import com.sonalune.pbp.model.Song;
import com.sonalune.pbp.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class HistoryController {
    private static final String TAG = "HistoryController";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public interface CapsuleDataListener {
        void onDataLoaded(User user, List<Song> topSongs, List<Singer> topArtists);
        void onError(String message);
    }

    public void getCapsuleData(CapsuleDataListener listener) {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) {
            listener.onError("User not logged in.");
            return;
        }

        checkAndResetMonthlyData(userId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fetchAndCalculateUserCapsuleData(userId, listener);
            } else {
                listener.onError(task.getException() != null ? task.getException().getMessage() : "Failed to check monthly data.");
            }
        });
    }

    private void fetchAndCalculateUserCapsuleData(String userId, CapsuleDataListener listener) {
        Task<DocumentSnapshot> userTask = db.collection("User").document(userId).get();
        Task<QuerySnapshot> historyTask = db.collection("History").whereEqualTo("userId", userId).get();

        Tasks.whenAllSuccess(userTask, historyTask).addOnSuccessListener(results -> {
            DocumentSnapshot userDoc = (DocumentSnapshot) results.get(0);
            User user = userDoc.exists() ? userDoc.toObject(User.class) : new User();
            if(user != null) user.setId(userDoc.getId());

            QuerySnapshot historySnap = (QuerySnapshot) results.get(1);

            List<String> topSongIds = calculateTopIdsFromHistory(historySnap, "songId");

            calculateAndFetchTopArtists(historySnap, topArtists -> {
                fetchSongDetailsByIds(topSongIds, topSongs -> {
                    listener.onDataLoaded(user, topSongs, topArtists);
                });
            });

        }).addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    private List<String> calculateTopIdsFromHistory(QuerySnapshot historySnapshot, String fieldNameToCount) {
        Map<String, Integer> counts = new HashMap<>();
        for (DocumentSnapshot doc : historySnapshot) {
            String id = doc.getString(fieldNameToCount);
            if (id != null) {
                counts.put(id, counts.getOrDefault(id, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(counts.entrySet());
        sortedList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        List<String> topIds = new ArrayList<>();
        for (int i = 0; i < Math.min(5, sortedList.size()); i++) {
            topIds.add(sortedList.get(i).getKey());
        }
        return topIds;
    }

    private void calculateAndFetchTopArtists(QuerySnapshot historySnapshot, TopDetailsListener<Singer> listener) {
        if (historySnapshot.isEmpty()) {
            listener.onDetailsLoaded(new ArrayList<>());
            return;
        }

        Set<String> uniqueSongIds = new HashSet<>();
        List<String> allSongListenEvents = new ArrayList<>();
        for (DocumentSnapshot doc : historySnapshot) {
            String songId = doc.getString("songId");
            if (songId != null) {
                uniqueSongIds.add(songId);
                allSongListenEvents.add(songId);
            }
        }

        if (uniqueSongIds.isEmpty()){
            listener.onDetailsLoaded(new ArrayList<>());
            return;
        }

        db.collection("Song").whereIn(FieldPath.documentId(), new ArrayList<>(uniqueSongIds)).get()
                .addOnSuccessListener(songDetailsSnap -> {
                    Map<String, String> songToSingerMap = new HashMap<>();
                    for (DocumentSnapshot songDoc : songDetailsSnap) {
                        songToSingerMap.put(songDoc.getId(), songDoc.getString("singerId"));
                    }

                    Map<String, Integer> singerCounts = new HashMap<>();
                    for (String songId : allSongListenEvents) {
                        String singerId = songToSingerMap.get(songId);
                        if (singerId != null) {
                            singerCounts.put(singerId, singerCounts.getOrDefault(singerId, 0) + 1);
                        }
                    }

                    List<Map.Entry<String, Integer>> sortedArtists = new ArrayList<>(singerCounts.entrySet());
                    sortedArtists.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
                    List<String> topArtistIds = new ArrayList<>();
                    for (int i = 0; i < Math.min(5, sortedArtists.size()); i++) {
                        topArtistIds.add(sortedArtists.get(i).getKey());
                    }

                    if (topArtistIds.isEmpty()) {
                        listener.onDetailsLoaded(new ArrayList<>());
                        return;
                    }

                    db.collection("Singer").whereIn(FieldPath.documentId(), topArtistIds).get()
                            .addOnSuccessListener(singerDetailsSnap -> {
                                List<Singer> unsortedArtists = new ArrayList<>();
                                for(DocumentSnapshot doc : singerDetailsSnap) {
                                    Singer singer = doc.toObject(Singer.class);
                                    if (singer != null) {
                                        singer.setId(doc.getId());
                                        unsortedArtists.add(singer);
                                    }
                                }
                                List<Singer> sortedArtist = new ArrayList<>();
                                for (String id : topArtistIds) {
                                    for (Singer singer : unsortedArtists) {
                                        if (id.equals(singer.getId())) {
                                            sortedArtist.add(singer);
                                            break;
                                        }
                                    }
                                }
                                listener.onDetailsLoaded(sortedArtist);
                            });
                });
    }

    private void fetchSongDetailsByIds(List<String> topSongIds, TopDetailsListener<Song> listener) {
        if (topSongIds.isEmpty()) {
            listener.onDetailsLoaded(new ArrayList<>());
            return;
        }
        db.collection("Song").whereIn(FieldPath.documentId(), topSongIds).get()
                .addOnSuccessListener(songSnap -> {
                    List<Song> unsortedSongs = new ArrayList<>();
                    for(DocumentSnapshot doc : songSnap) {
                        Song song = doc.toObject(Song.class);
                        if(song != null) {
                            song.setId(doc.getId());
                            unsortedSongs.add(song);
                        }
                    }
                    List<Song> sortedSongs = new ArrayList<>();
                    for (String id : topSongIds) {
                        for (Song song : unsortedSongs) {
                            if (id.equals(song.getId())) {
                                sortedSongs.add(song);
                                break;
                            }
                        }
                    }
                    listener.onDetailsLoaded(sortedSongs);
                });
    }

    private interface TopDetailsListener<T> {
        void onDetailsLoaded(List<T> items);
    }

    private Task<Void> checkAndResetMonthlyData(String userId) {
        DocumentReference userRef = db.collection("User").document(userId);

        return userRef.get().continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }

            DocumentSnapshot document = task.getResult();
            if (document.exists()) {
                User user = document.toObject(User.class);
                if (user == null) return Tasks.forResult(null);

                String lastMonth = user.getLastListenMonth();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
                String currentMonth = sdf.format(new Date());

                if (lastMonth == null || !lastMonth.equals(currentMonth)) {
                    Log.d(TAG, "Month changed. Resetting monthly stats for user: " + userId);
                    return userRef.update(
                            "monthlyListenSeconds", 0,
                            "lastListenMonth", currentMonth
                    );
                }
            }
            return Tasks.forResult(null);
        });
    }

    public void recordListenEvent(Song song, int durationInSeconds) {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null || song == null || song.getId() == null || durationInSeconds <= 0) {
            Log.w(TAG, "Invalid data, skipping history record.");
            return;
        }

        History history = new History(null, song.getId(), userId);
        db.collection("History").add(history)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "History event created successfully."))
                .addOnFailureListener(e -> Log.w(TAG, "Error creating history event.", e));

        DocumentReference userRef = db.collection("User").document(userId);
        userRef.update(
                "totalListenSeconds", FieldValue.increment(durationInSeconds),
                "monthlyListenSeconds", FieldValue.increment(durationInSeconds)
        ).addOnFailureListener(e -> Log.w(TAG, "Error updating user listen stats.", e));
    }
}