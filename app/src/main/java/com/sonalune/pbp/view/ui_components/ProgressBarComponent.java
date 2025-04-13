package com.sonalune.pbp.view.ui_components;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.sonalune.pbp.R;

public class ProgressBarComponent extends FrameLayout {

    private ProgressBar progressBar;

    public ProgressBarComponent(Context context) {
        super(context);
        init(context);
    }

    public ProgressBarComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProgressBarComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.fragment_progress_bar, this, true);
        progressBar = findViewById(R.id.progressbar);
    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress);
    }

    public void setMax(int max) {
        progressBar.setMax(max);
    }

    public void changeColor(String progressTint, String progressBackgroundTint) {
        int progressColor = Color.parseColor(progressTint);
        int backgroundColor = Color.parseColor(progressBackgroundTint);

        progressBar.setProgressTintList(ColorStateList.valueOf(progressColor));
        progressBar.setProgressBackgroundTintList(ColorStateList.valueOf(backgroundColor));
    }

}
