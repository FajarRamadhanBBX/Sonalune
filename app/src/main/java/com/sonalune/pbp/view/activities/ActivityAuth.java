package com.sonalune.pbp.view.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.sonalune.pbp.R;
import com.sonalune.pbp.controller.ManagementUser;

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

public class ActivityAuth extends AppCompatActivity implements ManagementUser.AuthListener {
    private ManagementUser managementUser;
    LinearLayout layoutSignUp, layoutSignIn, tabLayout;
    Button btnTabSignUp, btnTabSignIn, btnSignUp, btnSignIn;
    EditText fullNameSignUp, emailSignUp, passwordSignUp, emailSignIn, passwordSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent home = new Intent(ActivityAuth.this, MainActivity.class);
            home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(home);
            finish();
            return;
        }

        managementUser = new ManagementUser(this);
        layoutSignUp = findViewById(R.id.layoutSignUp);
        layoutSignIn = findViewById(R.id.layoutSignIn);
        btnTabSignIn = findViewById(R.id.btnTabSignIn);
        btnTabSignUp = findViewById(R.id.btnTabSignUp);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);
        tabLayout = findViewById(R.id.tabLayout);
        fullNameSignUp = findViewById(R.id.nameSignUp);
        emailSignUp = findViewById(R.id.emailSignUp);
        passwordSignUp = findViewById(R.id.passwordSignUp);
        emailSignIn = findViewById(R.id.emailSignIn);
        passwordSignIn = findViewById(R.id.passwordSignIn);

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

        btnSignUp.setOnClickListener(v -> {
            String fullName = fullNameSignUp.getText().toString().trim();
            String email = emailSignUp.getText().toString().trim();
            String password = passwordSignUp.getText().toString().trim();

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }
            managementUser.signUp(fullName, email, password, this);
        });

        btnSignIn.setOnClickListener(v -> {
            String email = emailSignIn.getText().toString().trim();
            String password = passwordSignIn.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            managementUser.signIn(email, password, this);
        });
    }

    @Override
    public void onSuccess(String message) {
        // Jika sign up atau sign in berhasil
        Toast.makeText(this, "Proses berhasil!", Toast.LENGTH_SHORT).show();
        // Pindah ke halaman utama
        Intent home = new Intent(ActivityAuth.this, MainActivity.class);
        home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(home);
        finish();
    }

    @Override
    public void onFailure(String message) {
        // Jika gagal, tampilkan pesan error dari controller
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
    }
}
