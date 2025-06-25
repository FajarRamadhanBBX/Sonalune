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
import java.util.Calendar;
import java.util.Collections;
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
        void onDataLoaded(User user, List<Song> topSongsAllTime, List<Singer> topArtistsAllTime, List<Song> topSongsMonthly, List<Singer> topArtistsMonthly);
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
                fetchAndProcessData(userId, listener);
            } else {
                listener.onError(task.getException() != null ? task.getException().getMessage() : "Failed to check monthly data.");
            }
        });
    }

    private void fetchAndProcessData(String userId, CapsuleDataListener listener) {
        // LANGKAH 1: Ambil semua data mentah yang diperlukan secara paralel
        Task<DocumentSnapshot> userTask = db.collection("User").document(userId).get();
        Task<QuerySnapshot> allHistoryTask = db.collection("History").whereEqualTo("userId", userId).get();
        Task<QuerySnapshot> monthlyHistoryTask = getMonthlyHistoryTask(userId);

        Tasks.whenAllSuccess(userTask, allHistoryTask, monthlyHistoryTask).addOnSuccessListener(results -> {
            DocumentSnapshot userDoc = (DocumentSnapshot) results.get(0);
            QuerySnapshot allHistorySnap = (QuerySnapshot) results.get(1);
            QuerySnapshot monthlyHistorySnap = (QuerySnapshot) results.get(2);

            User user = userDoc.exists() ? userDoc.toObject(User.class) : new User();
            if(user != null) user.setId(userDoc.getId());

            List<String> topSongIdsAllTime = calculateTopIdsFromHistory(allHistorySnap, "songId");
            List<String> topSongIdsMonthly = calculateTopIdsFromHistory(monthlyHistorySnap, "songId");

            Set<String> allUniqueSongIds = new HashSet<>();
            for (DocumentSnapshot doc : allHistorySnap) { allUniqueSongIds.add(doc.getString("songId")); }
            for (DocumentSnapshot doc : monthlyHistorySnap) { allUniqueSongIds.add(doc.getString("songId")); }

            if(allUniqueSongIds.isEmpty()) {
                listener.onDataLoaded(user, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                return;
            }

            db.collection("Song").whereIn(FieldPath.documentId(), new ArrayList<>(allUniqueSongIds)).get().addOnSuccessListener(songDetailsSnap -> {
                Map<String, String> songToSingerMap = new HashMap<>();
                for(DocumentSnapshot doc : songDetailsSnap) { songToSingerMap.put(doc.getId(), doc.getString("singerId")); }

                List<String> topArtistIdsAllTime = calculateTopArtistIds(allHistorySnap, songToSingerMap);
                List<String> topArtistIdsMonthly = calculateTopArtistIds(monthlyHistorySnap, songToSingerMap);

                finalizeAndSendData(user, topSongIdsAllTime, topArtistIdsAllTime, topSongIdsMonthly, topArtistIdsMonthly, listener);
            }).addOnFailureListener(e -> listener.onError(e.getMessage()));

        }).addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    private void finalizeAndSendData(User user, List<String> topSongIdsAllTime, List<String> topArtistIdsAllTime, List<String> topSongIdsMonthly, List<String> topArtistIdsMonthly, CapsuleDataListener listener) {
        Set<String> allSongIds = new HashSet<>(topSongIdsAllTime);
        allSongIds.addAll(topSongIdsMonthly);
        Set<String> allArtistIds = new HashSet<>(topArtistIdsAllTime);
        allArtistIds.addAll(topArtistIdsMonthly);

        Task<QuerySnapshot> songsTask = allSongIds.isEmpty() ? Tasks.forResult(null) : db.collection("Song").whereIn(FieldPath.documentId(), new ArrayList<>(allSongIds)).get();
        Task<QuerySnapshot> artistsTask = allArtistIds.isEmpty() ? Tasks.forResult(null) : db.collection("Singer").whereIn(FieldPath.documentId(), new ArrayList<>(allArtistIds)).get();

        Tasks.whenAllSuccess(songsTask, artistsTask).addOnSuccessListener(details -> {
            Map<String, Song> songMap = buildSongMap((QuerySnapshot) details.get(0));
            Map<String, Singer> artistMap = buildSingerMap((QuerySnapshot) details.get(1));

            List<Song> finalTopSongsAllTime = sortByIds(songMap, topSongIdsAllTime);
            List<Singer> finalTopArtistsAllTime = sortByIds(artistMap, topArtistIdsAllTime);
            List<Song> finalTopSongsMonthly = sortByIds(songMap, topSongIdsMonthly);
            List<Singer> finalTopArtistsMonthly = sortByIds(artistMap, topArtistIdsMonthly);

            listener.onDataLoaded(user, finalTopSongsAllTime, finalTopArtistsAllTime, finalTopSongsMonthly, finalTopArtistsMonthly);
        }).addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    private List<String> calculateTopIdsFromHistory(QuerySnapshot historySnapshot, String fieldNameToCount) {
        if (historySnapshot == null || historySnapshot.isEmpty()) return new ArrayList<>();
        Map<String, Integer> counts = new HashMap<>();
        for (DocumentSnapshot doc : historySnapshot) {
            String id = doc.getString(fieldNameToCount);
            if (id != null) counts.put(id, counts.getOrDefault(id, 0) + 1);
        }
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(counts.entrySet());
        Collections.sort(sortedList, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        List<String> topIds = new ArrayList<>();
        for (int i = 0; i < Math.min(5, sortedList.size()); i++) topIds.add(sortedList.get(i).getKey());
        return topIds;
    }

    private List<String> calculateTopArtistIds(QuerySnapshot historySnapshot, Map<String, String> songToSingerMap) {
        if (historySnapshot == null || historySnapshot.isEmpty()) return new ArrayList<>();
        Map<String, Integer> singerCounts = new HashMap<>();
        for (DocumentSnapshot doc : historySnapshot) {
            String songId = doc.getString("songId");
            String singerId = songToSingerMap.get(songId);
            if (singerId != null) {
                singerCounts.put(singerId, singerCounts.getOrDefault(singerId, 0) + 1);
            }
        }
        // Lakukan sorting dan ambil 5 teratas (sama seperti di calculateTopIdsFromHistory)
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(singerCounts.entrySet());
        Collections.sort(sortedList, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        List<String> topIds = new ArrayList<>();
        for (int i = 0; i < Math.min(5, sortedList.size()); i++) topIds.add(sortedList.get(i).getKey());
        return topIds;
    }

    private Task<QuerySnapshot> getMonthlyHistoryTask(String userId) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        Date startOfMonth = calendar.getTime();
        return db.collection("History")
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("playTime", startOfMonth)
                .get();
    }

    private Map<String, Song> buildSongMap(QuerySnapshot songSnap) {
        Map<String, Song> map = new HashMap<>();
        if (songSnap != null) {
            for (DocumentSnapshot doc : songSnap) {
                Song item = doc.toObject(Song.class);
                if (item != null) { item.setId(doc.getId()); map.put(doc.getId(), item); }
            }
        }
        return map;
    }

    private Map<String, Singer> buildSingerMap(QuerySnapshot artistSnap) {
        Map<String, Singer> map = new HashMap<>();
        if (artistSnap != null) {
            for (DocumentSnapshot doc : artistSnap) {
                Singer item = doc.toObject(Singer.class);
                if (item != null) { item.setId(doc.getId()); map.put(doc.getId(), item); }
            }
        }
        return map;
    }

    private <T> List<T> sortByIds(Map<String, T> map, List<String> sortedIds) {
        List<T> sortedList = new ArrayList<>();
        if (map == null || sortedIds == null) return sortedList;
        for (String id : sortedIds) {
            if (map.containsKey(id)) {
                sortedList.add(map.get(id));
            }
        }
        return sortedList;
    }

    private interface TopIdsListener { void onIdsCalculated(List<String> topIds); }

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

        History history = new History(song.getId(), userId);
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