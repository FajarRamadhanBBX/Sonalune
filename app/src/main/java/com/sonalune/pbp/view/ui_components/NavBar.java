package com.sonalune.pbp.view.ui_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.sonalune.pbp.R;

public class NavBar extends LinearLayout {

    public NavBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.sample_nav_bar, this);
        initViews();
    }

    private void initViews() {
        LinearLayout home = findViewById(R.id.nav_home);
        LinearLayout caps = findViewById(R.id.nav_capsuled);
        LinearLayout profile = findViewById(R.id.nav_profile);

        home.setOnClickListener(v -> selectTab("home"));
        caps.setOnClickListener(v -> selectTab("capsuled"));
        profile.setOnClickListener(v -> selectTab("profile"));
    }

    private void selectTab(String tab) {
        clearSelection();

        LinearLayout selected;
        switch (tab) {
            case "home":
                selected = findViewById(R.id.nav_home);
                break;
            case "capsuled":
                selected = findViewById(R.id.nav_capsuled);
                break;
            case "profile":
                selected = findViewById(R.id.nav_profile);
                break;
            default:
                return;
        }

        selected.setBackgroundResource(R.drawable.bg_navbar_selected);
        // trigger callback kalau perlu
        if (listener != null) listener.onTabSelected(tab);
    }

    private void clearSelection() {
        findViewById(R.id.nav_home).setBackground(null);
        findViewById(R.id.nav_capsuled).setBackground(null);
        findViewById(R.id.nav_profile).setBackground(null);
    }

    private NavBarListener listener;

    public void setNavBarListener(NavBarListener listener) {
        this.listener = listener;
    }

    public interface NavBarListener {
        void onTabSelected(String tab);
    }
}
