package com.sonalune.pbp.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class User {
    private String id;
    private String fullname;
    private String email;
    private String password;
    private String photo;
    @ServerTimestamp
    private Date createdAt;
    @ServerTimestamp
    private Date updatedAt;

    public User(){}

    public User(String id, String fullname, String email, String password) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.photo = "https://bzvdoaouvekmijrdgmbz.supabase.co/storage/v1/object/sign/uasplatform/image_song/semoga.jpeg?token=eyJraWQiOiJzdG9yYWdlLXVybC1zaWduaW5nLWtleV9mYTNiMGUwZS0wZTMxLTQyOTEtOWJmYS0zNjk5MmQ0ZGM5ZDMiLCJhbGciOiJIUzI1NiJ9.eyJ1cmwiOiJ1YXNwbGF0Zm9ybS9pbWFnZV9zb25nL3NlbW9nYS5qcGVnIiwiaWF0IjoxNzUwNjQ2MDExLCJleHAiOjE3ODIxODIwMTF9.wX9ZyxdUD8RkHcS5sIt4NTiLLVPeQYT5zyzVhoIf008";
        this.password = password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }


    public  String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoto() { return photo; }

    public void setPhoto(String photo) { this.photo = photo; }
}