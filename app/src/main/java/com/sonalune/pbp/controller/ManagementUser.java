package com.sonalune.pbp.controller;

import android.content.Context;
import android.util.Log;

import com.sonalune.pbp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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

    public ManagementUser(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public void signUp(String fullname, String email, String password, AuthListener authListener){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "FirebaseAuth: User created successfully.");
                        String uid = auth.getCurrentUser().getUid();
                        User user = new User(uid, fullname, email, password);

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
                        listener.onSuccess("Success"); // Panggil callback sukses
                    } else {
                        Log.w(TAG, "FirebaseAuth: signInWithEmail:failure", task.getException());
                        listener.onFailure("Authentication failed. Email atau Password Salah."); // Panggil callback gagal
                    }
                });
    }

    public void singOut() {
        FirebaseAuth.getInstance().signOut();
    }
}
