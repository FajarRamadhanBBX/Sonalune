package com.sonalune.pbp.model;

import com.google.firebase.firestore.Exclude;
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


    // --- Getters and Setters ---
    @Exclude
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