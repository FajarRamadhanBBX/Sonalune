package com.sonalune.pbp.view.ui_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.cardview.widget.CardView;

import com.sonalune.pbp.R;

/**
 * TODO: document your custom view class.
 */

public class PlayerCardView extends CardView {

    public PlayerCardView(Context context) {
        super(context);
        init(context);
    }

    public PlayerCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlayerCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.sample_player_card_view, this, true);

        // Contoh: akses view-nya
        ProgressBar progress = findViewById(R.id.progress);
        ImageView albumImage = findViewById(R.id.album_image);
        ImageButton btnPause = findViewById(R.id.btn_pause);
        // dst sesuai kebutuhan
    }
}
