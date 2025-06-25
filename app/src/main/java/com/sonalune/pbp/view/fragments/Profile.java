package com.sonalune.pbp.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.sonalune.pbp.R;
import com.sonalune.pbp.controller.ManagementUser;
import com.sonalune.pbp.model.User;
import com.sonalune.pbp.view.activities.ActivityAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends Fragment {

    private LinearLayout btnEditAndLogout;
    private Button btnEditProfile, btnLogout, btnSaveProfile;
    private ImageView btnBack;
    private ImageButton icEditName;
    private TextView headerProfile, profileNameHeader;
    private EditText editUserName, editUserEmail;
    private CircleImageView imgProfile, icEditPhotoProfile;

    private ManagementUser managementUser;
    private User currentUser;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        managementUser = new ManagementUser(getContext());

        // Inisialisasi semua view
        initViews(view);

        // Muat data pengguna
        loadUserProfile();

        // Atur semua listener
        setupListeners();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    private void initViews(View view) {
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnBack = view.findViewById(R.id.btn_back);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);
        btnEditAndLogout = view.findViewById(R.id.btnEditAndLogout);
        icEditName = view.findViewById(R.id.ic_edit_name);
        headerProfile = view.findViewById(R.id.header_profile);
        icEditPhotoProfile = view.findViewById(R.id.ic_edit_photo_profile);

        imgProfile = view.findViewById(R.id.imgProfile);
        profileNameHeader = view.findViewById(R.id.profileNameHeader);
        editUserName = view.findViewById(R.id.user_name);
        editUserEmail = view.findViewById(R.id.user_email);
    }

    private void loadUserProfile() {
        managementUser.getCurrentUserData(new ManagementUser.UserListener() {
            @Override
            public void onUserLoaded(User user) {
                currentUser = user;
                profileNameHeader.setText(user.getFullname());
                editUserName.setText(user.getFullname());
                editUserEmail.setText(user.getEmail());

                if (user.getPhoto() != null && !user.getPhoto().isEmpty()) {
                    Glide.with(getContext()).load(user.getPhoto()).into(imgProfile);
                } else {
                    // Tampilkan gambar default jika tidak ada foto profil
                    imgProfile.setImageResource(R.drawable.im_antony);
                }
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        btnEditProfile.setOnClickListener(v -> setEditMode(true));
        btnSaveProfile.setOnClickListener(v -> saveProfileChanges());
        btnBack.setOnClickListener(v -> setEditMode(false));

        icEditName.setOnClickListener(v -> {
            setEditable(editUserName, true);
            editUserName.requestFocus();
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent signUpPage = new Intent(getActivity(), ActivityAuth.class);
            signUpPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signUpPage);
        });
    }

    private void setEditMode(boolean isEditing) {
        if (isEditing) {
            btnEditAndLogout.setVisibility(View.GONE);
            icEditPhotoProfile.setVisibility(View.VISIBLE);
            btnSaveProfile.setVisibility(View.VISIBLE);
            icEditName.setVisibility(View.VISIBLE);
            headerProfile.setText("Edit Profile");
            btnBack.setVisibility(View.VISIBLE);
        } else {
            btnEditAndLogout.setVisibility(View.VISIBLE);
            icEditPhotoProfile.setVisibility(View.GONE);
            btnSaveProfile.setVisibility(View.GONE);
            icEditName.setVisibility(View.GONE);
            headerProfile.setText("Profile");
            btnBack.setVisibility(View.GONE);

            setEditable(editUserName, false);
            if (currentUser != null) {
                editUserName.setText(currentUser.getFullname());
            }
        }
    }

    private void saveProfileChanges() {
        String newFullName = editUserName.getText().toString().trim();

        if (newFullName.isEmpty()) {
            editUserName.setError("Full name is required");
            return;
        }

        managementUser.updateUserProfile(newFullName, new ManagementUser.AuthListener() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                setEditMode(false);
                loadUserProfile();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setEditable(EditText editText, boolean isEditable) {
        editText.setFocusable(isEditable);
        editText.setFocusableInTouchMode(isEditable);
        editText.setClickable(isEditable);
    }
}