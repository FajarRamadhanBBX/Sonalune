package com.sonalune.pbp.view.ui_components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.sonalune.pbp.R;

public class PlayerCardView extends CardView {

    private ImageView songImage;
    private TextView songTitle;
    private TextView singerName;
    private ProgressBar progressBar;
    private ImageButton btnPause, btnNext, btnPlay;
    private OnClickListener playClickListener, pauseClickListener, nextClickListener;

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
        LayoutInflater.from(context).inflate(R.layout.ui_player_card_view, this, true);

        songImage = findViewById(R.id.songImage);
        songTitle = findViewById(R.id.songTitle);
        singerName = findViewById(R.id.singerName);
        progressBar = findViewById(R.id.progressBar);
        btnPause = findViewById(R.id.btnPauseCardView);
        btnNext = findViewById(R.id.btnNextCardView);
        btnPlay = findViewById(R.id.btnPlayCardView);

        btnPlay.setOnClickListener(v -> {
            if (playClickListener != null) {
                playClickListener.onClick(v);
            }
        });

        btnPause.setOnClickListener(v -> {
            if (pauseClickListener != null) {
                pauseClickListener.onClick(v);
            }
        });

        btnNext.setOnClickListener(v -> {
            if (nextClickListener != null) {
                nextClickListener.onClick(v);
            }
        });
    }

    public void showAsPlaying(boolean isPlaying) {
        if (isPlaying) {
            btnPlay.setVisibility(View.GONE);
            btnPause.setVisibility(View.VISIBLE);
        } else {
            btnPlay.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.GONE);
        }
    }

    // ====== Public Methods untuk Mengatur UI dan Listener ======
    public void setAlbumImage(String imageUrl) {
        Glide.with(songImage).load(imageUrl).into(songImage);
    }

    public void setSongTitle(String title) {
        songTitle.setText(title);
    }

    public void setSingerName(String name) {
        singerName.setText(name);
    }

    public void setProgress(int currentMillis) {
        progressBar.setProgress(currentMillis);
    }

    public void setMaxProgress(int maxMillis) {
        progressBar.setMax(maxMillis);
    }


    public void setOnPlayClickListener(OnClickListener listener) {
        this.playClickListener = listener;
    }

    public void setOnPauseClickListener(OnClickListener listener) {
        this.pauseClickListener = listener;
    }

    public void setOnNextClickListener(OnClickListener listener) {
        this.nextClickListener = listener;
    }
}