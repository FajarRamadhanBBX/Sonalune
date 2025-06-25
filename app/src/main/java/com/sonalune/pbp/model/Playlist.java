package com.sonalune.pbp.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Playlist {
    private String id;
    private String userId;
    private String name;
    private Boolean isPublic;
    private String imageUrl;
    private List<String> songId;
    @ServerTimestamp
    private Date createdAt;
    @ServerTimestamp
    private Date updatedAt;

    public Playlist() {}

    public Playlist(String userId, String name, Boolean isPublic, String imageUrl) {
        this.userId = userId;
        this.isPublic = isPublic;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    // --- Method Stubs ---
    public void addPlaylist() {
        // TODO: Implementasi logika method
    }

    public void updatePlaylist() {
        // TODO: Implementasi logika method
    }

    public void deletePlaylist() {
        // TODO: Implementasi logika method
    }

    public void addSongToPlaylist() {
        // TODO: Implementasi logika method
    }

    public void deleteSongFromPlaylist() {
        // TODO: Implementasi logika method
    }


    // --- Getters and Setters ---

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<String> getSongId() {
        return songId;
    }

    public void setSongId(List<String> songId) {
        this.songId = songId;
    }
}