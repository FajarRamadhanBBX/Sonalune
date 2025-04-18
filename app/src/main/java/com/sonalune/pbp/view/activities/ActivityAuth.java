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
    LinearLayout layoutSignUp, layoutSignIn, tabLayout;
    Button btnTabSignUp, btnTabSignIn, btnSignUp, btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        layoutSignUp = findViewById(R.id.layoutSignUp);
        layoutSignIn = findViewById(R.id.layoutSignIn);
        btnTabSignIn = findViewById(R.id.btnTabSignIn);
        btnTabSignUp = findViewById(R.id.btnTabSignUp);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);
        tabLayout = findViewById(R.id.tabLayout);

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

        btnTabSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutSignUp.setVisibility(View.VISIBLE);
                layoutSignIn.setVisibility(View.GONE);
                btnTabSignIn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FCC8D1")));
                btnTabSignIn.setTextColor(ColorStateList.valueOf(Color.parseColor("#D14D72")));
                btnTabSignUp.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D14D72")));
                btnTabSignUp.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v){
               Intent home = new Intent(ActivityAuth.this, MainActivity.class);
               startActivity(home);
           }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent home = new Intent(ActivityAuth.this, MainActivity.class);
                startActivity(home);
            }
        });
    }
}
