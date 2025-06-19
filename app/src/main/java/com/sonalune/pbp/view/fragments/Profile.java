package com.sonalune.pbp.view.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.sonalune.pbp.R;
import com.sonalune.pbp.view.activities.ActivityAuth;
import com.sonalune.pbp.view.activities.MainActivity;

public class Profile extends Fragment {

    LinearLayout btnEditAndLogout;
    Button btnEditProfile, btnLogout, btnSaveProfile;
    ImageView btnBack;
    ImageButton ic_edit_name, ic_edit_email, ic_edit_password;
    TextView header_profile;

    de.hdodenhof.circleimageview.CircleImageView ic_edit_photo_profile;

    public Profile() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnBack = view.findViewById(R.id.btn_back);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);
        btnEditAndLogout = view.findViewById(R.id.btnEditAndLogout);
        ic_edit_name = view.findViewById(R.id.ic_edit_name);
        ic_edit_email = view.findViewById(R.id.ic_edit_email);
        ic_edit_password = view.findViewById(R.id.ic_edit_password);
        header_profile = view.findViewById(R.id.header_profile);
        ic_edit_photo_profile = view.findViewById(R.id.ic_edit_photo_profile);

        // initially hide the back button
        btnBack.setVisibility(View.GONE);

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEditAndLogout.setVisibility(View.GONE);
                ic_edit_photo_profile.setVisibility(View.VISIBLE);
                btnSaveProfile.setVisibility(View.VISIBLE);
                ic_edit_name.setVisibility(View.VISIBLE);
                ic_edit_email.setVisibility(View.VISIBLE);
                ic_edit_password.setVisibility(View.VISIBLE);
                header_profile.setText("Edit Profile");
                btnBack.setVisibility(View.VISIBLE);
            }
        });

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEditAndLogout.setVisibility(View.VISIBLE);
                ic_edit_photo_profile.setVisibility(View.GONE);
                btnSaveProfile.setVisibility(View.GONE);
                ic_edit_name.setVisibility(View.GONE);
                ic_edit_email.setVisibility(View.GONE);
                ic_edit_password.setVisibility(View.GONE);
                header_profile.setText("Profile");
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button logic (e.g., exit edit state)
                btnEditAndLogout.setVisibility(View.VISIBLE);
                ic_edit_photo_profile.setVisibility(View.GONE);
                btnSaveProfile.setVisibility(View.GONE);
                ic_edit_name.setVisibility(View.GONE);
                ic_edit_email.setVisibility(View.GONE);
                ic_edit_password.setVisibility(View.GONE);
                header_profile.setText("Profile");

                // Hide the back button
                btnBack.setVisibility(View.GONE);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent signUpPage = new Intent(getActivity(), ActivityAuth.class);
                signUpPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signUpPage);
            }
        });
        return view;
    }
}