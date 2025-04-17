package com.sonalune.pbp.view.ui_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.sonalune.pbp.R;

/**
 * TODO: document your custom view class.
 */

public class ProgressBarPlayerScreen extends FrameLayout {

    private SeekBar ProgressBarPlayerScreen;

    public ProgressBarPlayerScreen(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.sample_progress_bar_player_screen, this, true);
        ProgressBarPlayerScreen = findViewById(R.id.progressBar);
    }

    public ProgressBarPlayerScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
}