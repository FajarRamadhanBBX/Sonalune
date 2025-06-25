package com.sonalune.pbp.controller;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.sonalune.pbp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ManagementUser {
    User user;
    private static final String TAG = "ManagementUser";
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Context context;

    public interface AuthListener {
        void onSuccess(String message);
        void onFailure(String message);
    }

    public interface UserListener {
        void onUserLoaded(User user);
        void onFailure(String message);
    }

    public ManagementUser(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public void getCurrentUserData(UserListener listener) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            db.collection("User").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                user.setId(documentSnapshot.getId());
                                listener.onUserLoaded(user);
                            } else {
                                listener.onFailure("Failed to parse user data.");
                            }
                        } else {
                            listener.onFailure("User data not found in Firestore.");
                        }
                    })
                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
        } else {
            listener.onFailure("No user is currently signed in.");
        }
    }

    public void updateUserProfile(String newFullName, AuthListener listener) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            listener.onFailure("No user logged in.");
            return;
        }
        String uid = firebaseUser.getUid();
        Map<String, Object> updates = new HashMap<>();
        updates.put("fullname", newFullName);

        db.collection("User").document(uid).update(updates)
                .addOnSuccessListener(aVoid -> listener.onSuccess("Profile updated successfully."))
                .addOnFailureListener(e -> listener.onFailure("Failed to update profile: " + e.getMessage()));
    }

    public void signUp(String fullname, String email, String password, AuthListener authListener){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "FirebaseAuth: User created successfully.");
                        String uid = auth.getCurrentUser().getUid();
                        User user = new User(fullname, email);

                        db.collection("User").document(uid).set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Firestore: User profile saved.");
                                    authListener.onSuccess("success");
                                })
                                .addOnFailureListener(e -> {
                                    Log.w(TAG, "Firestore: Error", e);
                                    authListener.onFailure("error");
                                });
                    } else {
                        Log.w(TAG, "FirebaseAuth: User creation failed.", task.getException());
                        authListener.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void signIn(String email, String password, AuthListener listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "FirebaseAuth: signInWithEmail:success");
                        listener.onSuccess("Success");
                    } else {
                        Log.w(TAG, "FirebaseAuth: signInWithEmail:failure", task.getException());
                        listener.onFailure("Authentication failed. Email atau Password Salah.");
                    }
                });
    }
}
