package com.sonalune.pbp.model;

public class Playlist {
    private String name;
    private int imageResId; // atau String imageUrl jika pakai Glide/Picasso

    public Playlist(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public int getImageResId() { return imageResId; }
}
