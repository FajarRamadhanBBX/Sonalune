package com.sonalune.pbp.view.ui_components;

import android.content.Context;
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
        LinearLayout home = findViewById(R.id.navHome);
        LinearLayout caps = findViewById(R.id.navCapsuled);
        LinearLayout profile = findViewById(R.id.navProfile);

        home.setOnClickListener(v -> selectTab("home"));
        caps.setOnClickListener(v -> selectTab("capsuled"));
        profile.setOnClickListener(v -> selectTab("profile"));
    }

    public void selectTab(String tab) {
        clearSelection();

        LinearLayout selected;
        View bg_selected;
        switch (tab) {
            case "home":
                selected = findViewById(R.id.navHome);
                bg_selected = findViewById(R.id.navHomeIcon);
                break;
            case "capsuled":
                selected = findViewById(R.id.navCapsuled);
                bg_selected = findViewById(R.id.navCapsuledIcon);
                break;
            case "profile":
                selected = findViewById(R.id.navProfile);
                bg_selected = findViewById(R.id.navProfileIcon);
                break;
            default:
                return;
        }

        bg_selected.setBackgroundResource(R.drawable.bg_navbar_selected);
        // trigger callback kalau perlu
        if (listener != null) listener.onTabSelected(tab);
    }

    private void clearSelection() {
        findViewById(R.id.navHomeIcon).setBackground(null);
        findViewById(R.id.navCapsuledIcon).setBackground(null);
        findViewById(R.id.navProfileIcon).setBackground(null);
    }

    private NavBarListener listener;

    public void setNavBarListener(NavBarListener listener) {
        this.listener = listener;
    }

    public interface NavBarListener {
        void onTabSelected(String tab);
    }
}
