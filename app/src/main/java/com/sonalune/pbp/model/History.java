package com.sonalune.pbp.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class History {
    private String id;
    private String songId;
    private String userId;
    @ServerTimestamp
    private Date playTime;
    @ServerTimestamp
    private Date createdAt;
    @ServerTimestamp
    private Date updatedAt;

    public History() {}

    public History(String songId, String userId) {
        this.songId = songId;
        this.userId = userId;
    }

    // --- Method Stubs ---
    public void addHistory() {
        // TODO: Implementasi logika method
    }

    public void getCapsule() {
        // TODO: Implementasi logika method
    }

    public void deleteHistory() {
        // TODO: Implementasi logika method
    }

    // --- Getters and Setters ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getPlayTime() {
        return playTime;
    }



    public void setPlayTime(Date playTime) {
        this.playTime = playTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}