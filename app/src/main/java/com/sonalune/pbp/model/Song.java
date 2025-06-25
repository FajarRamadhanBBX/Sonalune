package com.sonalune.pbp.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Song {
    private String id;
    private String title;
    private String singerId;
    private String songUrl;
    private String imageUrl;
    private int listenCount;
    private int duration;
    @ServerTimestamp
    private Date createdAt;
    @ServerTimestamp
    private Date updatedAt;

    public Song() {}

    public Song(String title, String singerId, int duration, String imageUrl, String songUrl) {
        this.title = title;
        this.singerId = singerId;
        this.songUrl = songUrl;
        this.imageUrl = imageUrl;
        this.listenCount = 0;
        this.duration = duration;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // --- Getters and Setters ---

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSingerId() {
        return singerId;
    }

    public void setSingerId(String singerId) {
        this.singerId = singerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getListenCount() {
        return listenCount;
    }

    public void setListenCount(int listenCount) {
        this.listenCount = listenCount;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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