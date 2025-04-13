package com.sonalune.pbp.view.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.sonalune.pbp.R;

//public class ActivityAuth extends AppCompatActivity {
//    
//    LinearLayout layoutSignUp
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_auth);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.auth), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//}

public class ActivityAuth extends AppCompatActivity {
    LinearLayout layoutSignUp, layoutSignIn;
    Button btnTabSignUp, btnTabSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        layoutSignUp = findViewById(R.id.layoutSignUp);
        layoutSignIn = findViewById(R.id.layoutSignIn);
        btnTabSignUp = findViewById(R.id.btnTabSignUp);
        btnTabSignIn = findViewById(R.id.btnTabSignIn);

        btnTabSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutSignUp.setVisibility(View.GONE);
                layoutSignIn.setVisibility(View.VISIBLE);
                btnTabSignIn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D14D72")));
                btnTabSignIn.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                btnTabSignUp.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FCC8D1")));
                btnTabSignUp.setTextColor(ColorStateList.valueOf(Color.parseColor("#D14D72")));
            }
        });

        // Handle Sign In button click
        Button btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your login validation logic here (e.g., check credentials)

                // Navigate to MainActivity after successful login
                Intent intent = new Intent(ActivityAuth.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });
    }}
