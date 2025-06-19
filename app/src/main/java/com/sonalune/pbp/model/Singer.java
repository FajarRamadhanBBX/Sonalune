package com.sonalune.pbp.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Singer {
    private String id;
    private String name;
    private int monthlyListener;
    private String imageUrl;
    @ServerTimestamp
    private Date createdAt;
    @ServerTimestamp
    private Date updatedAt;

    public Singer() {}

    public Singer(String id, String name, int monthlyListener, String imageUrl) {
        this.id = id;
        this.name = name;
        this.monthlyListener = monthlyListener;
        this.imageUrl = imageUrl;
    }

    // --- Getters and Setters ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMonthlyListener() {
        return monthlyListener;
    }

    public void setMonthlyListener(int monthlyListener) {
        this.monthlyListener = monthlyListener;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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