package com.sonalune.pbp.view.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.sonalune.pbp.R;

public class ActivityAuth extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}

//public class AuthActivity extends AppCompatActivity {
//
//    LinearLayout layoutSignUp, layoutSignIn;
//    Button btnTabSignUp, btnTabSignIn;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_auth);
//
//        layoutSignUp = findViewById(R.id.layoutSignUp);
//        layoutSignIn = findViewById(R.id.layoutSignIn);
//        btnTabSignUp = findViewById(R.id.btnTabSignUp);
//        btnTabSignIn = findViewById(R.id.btnTabSignIn);
//
//        btnTabSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                layoutSignUp.setVisibility(View.VISIBLE);
//                layoutSignIn.setVisibility(View.GONE);
//                btnTabSignUp.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D14F72")));
//                btnTabSignIn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FDD1D9")));
//            }
//        });
//
//        btnTabSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                layoutSignUp.setVisibility(View.GONE);
//                layoutSignIn.setVisibility(View.VISIBLE);
//                btnTabSignUp.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FDD1D9")));
//                btnTabSignIn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D14F72")));
//            }
//        });
//    }
//}
