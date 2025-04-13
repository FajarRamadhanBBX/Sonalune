//package com.sonalune.pbp.view.ui_components;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.drawable.Drawable;
//import android.text.TextPaint;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//
//import androidx.cardview.widget.CardView;
//
//import com.sonalune.pbp.R;
//
///**
// * TODO: document your custom view class.
// */
//
//public class PlayerCardView extends CardView {
//
//    public PlayerCardView(Context context) {
//        super(context);
//        init(context);
//    }
//
//    public PlayerCardView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init(context);
//    }
//
//    public PlayerCardView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init(context);
//    }
//
//    private void init(Context context) {
//        LayoutInflater.from(context).inflate(R.layout.sample_player_card_view, this, true);
//
//        // Contoh: akses view-nya
//        ProgressBar progress = findViewById(R.id.progress);
//        ImageView albumImage = findViewById(R.id.album_image);
//        ImageButton btnPause = findViewById(R.id.btn_pause);
//        // dst sesuai kebutuhan
//    }
//}

package com.sonalune.pbp.view.ui_components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.sonalune.pbp.R;

public class PlayerCardView extends CardView {

    private ImageView albumImage;
    private TextView songTitle;
    private TextView artistName;
    private ProgressBar progressBar;
    private ImageButton btnPause, btnNext;

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

        albumImage = findViewById(R.id.album_image);
        songTitle = findViewById(R.id.song_title);
        artistName = findViewById(R.id.artist_name);
        progressBar = findViewById(R.id.progress);
        btnPause = findViewById(R.id.btn_pause);
        btnNext = findViewById(R.id.btn_next);
    }

    // ====== Public Methods to Update UI ======
    public void setAlbumImage(int drawableResId) {
        albumImage.setImageResource(drawableResId);
    }

    public void setSongTitle(String title) {
        songTitle.setText(title);
    }

    public void setArtistName(String name) {
        artistName.setText(name);
    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress);
    }

    public void setOnPauseClickListener(OnClickListener listener) {
        btnPause.setOnClickListener(listener);
    }

    public void setOnNextClickListener(OnClickListener listener) {
        btnNext.setOnClickListener(listener);
    }
}
