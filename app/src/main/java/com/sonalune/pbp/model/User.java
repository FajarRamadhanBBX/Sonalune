package com.sonalune.pbp.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class User {
    private String id;
    private String fullname;
    private String email;
    private String password;
    private String photo;
    private int totalListenSeconds;
    private int monthlyListenSeconds;
    private String lastListenMonth;
    @ServerTimestamp
    private Date createdAt;
    @ServerTimestamp
    private Date updatedAt;

    public User(){}

    public User(String id, String fullname, String email, String password) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.photo = "https://bzvdoaouvekmijrdgmbz.supabase.co/storage/v1/object/sign/uasplatform/avatar/avatar-photo-profile.png?token=eyJraWQiOiJzdG9yYWdlLXVybC1zaWduaW5nLWtleV9mYTNiMGUwZS0wZTMxLTQyOTEtOWJmYS0zNjk5MmQ0ZGM5ZDMiLCJhbGciOiJIUzI1NiJ9.eyJ1cmwiOiJ1YXNwbGF0Zm9ybS9hdmF0YXIvYXZhdGFyLXBob3RvLXByb2ZpbGUucG5nIiwiaWF0IjoxNzUwNzQ4ODc0LCJleHAiOjE3ODIyODQ4NzR9.3Ss8E9ZWmGcPeBSnmH3B_tm0kwiv3sFU0Cii2jeA_Q0";
        this.password = password;
        this.totalListenSeconds = 0;
        this.monthlyListenSeconds = 0;
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

    public int getTotalListenSeconds() {
        return totalListenSeconds;
    }

    public void setTotalListenSeconds(int totalListenSeconds) {
        this.totalListenSeconds = totalListenSeconds;
    }

    public int getMonthlyListenSeconds() {
        return monthlyListenSeconds;
    }

    public void setMonthlyListenSeconds(int monthlyListenSeconds) {
        this.monthlyListenSeconds = monthlyListenSeconds;
    }

    public String getLastListenMonth() {
        return lastListenMonth;
    }

    public void setLastListenMonth(String lastListenMonth) {
        this.lastListenMonth = lastListenMonth;
    }
}