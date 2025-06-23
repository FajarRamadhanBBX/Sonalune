package com.sonalune.pbp.view.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.sonalune.pbp.R;
import com.sonalune.pbp.controller.SongController;
import com.sonalune.pbp.model.Singer;
import com.sonalune.pbp.model.Song;
import com.sonalune.pbp.view.activities.MainActivity;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PlayingScreen extends Fragment implements SongController.SongStateListener {

    private MainActivity mainActivity;
    private SongController songController;

    private ImageView imageAlbumArt, imageSmallAlbumArt, btnBack;
    private TextView textSongTitle, textSingerName, textCurrentTime, textRemainingTime;
    private SeekBar seekBar;
    private ImageButton btnPlay, btnPause, btnNext, btnPrev;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mainActivity = (MainActivity) context;
            // Dapatkan controller dari activity
            songController = mainActivity.getSongController();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playing_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack = view.findViewById(R.id.btn_back);
        imageAlbumArt = view.findViewById(R.id.largeImagePlayingScreen);
        imageSmallAlbumArt = view.findViewById(R.id.smallImagePlayingScreen);
        textSongTitle = view.findViewById(R.id.songTitlePlayingScreem);
        textSingerName = view.findViewById(R.id.singerNamePlayingScreem);
        textCurrentTime = view.findViewById(R.id.currentDuration);
        textRemainingTime = view.findViewById(R.id.remainingDuration);
        seekBar = view.findViewById(R.id.seekBarPlayingScreen);
        btnPlay = view.findViewById(R.id.btnPlayPlayingScreen);
        btnPause = view.findViewById(R.id.btnPausePlayingScreen);
        btnNext = view.findViewById(R.id.btnNextPlayingScreen);
        btnPrev = view.findViewById(R.id.btnPrevPlayingScreen);

        setupListeners();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> mainActivity.onBackPressed());
        btnPlay.setOnClickListener(v -> songController.resumeSong());
        btnPause.setOnClickListener(v -> songController.pauseSong());
        btnNext.setOnClickListener(v -> songController.playNextSong());
        btnPrev.setOnClickListener(v -> songController.playPreviousSong());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int userProgress;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    userProgress = progress;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                songController.seekTo(userProgress);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (songController != null) {
            songController.setSongStateListener(this);
            songController.requestUpdate();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (songController != null) {
            songController.setSongStateListener(mainActivity);
            songController.requestUpdate();
        }
    }

    @Override
    public void onSongChanged(Song newSong) {
        if (isAdded()) {
            String singerName = "Unknown Singer";
            textSongTitle.setText(newSong.getTitle());
            List<Singer> currentSingers = mainActivity.getCurrentSingers();

            if (currentSingers != null) {
                for (Singer s : currentSingers) {
                    if (s.getId() != null && s.getId().equals(newSong.getSingerId())) {
                        singerName = s.getName();
                        break;
                    }
                }
            }

            textSingerName.setText(singerName);
            Glide.with(this).load(newSong.getImageUrl()).into(imageAlbumArt);
            Glide.with(this).load(newSong.getImageUrl()).into(imageSmallAlbumArt);
        }
    }

    @Override
    public void onPlaybackStateChanged(boolean isPlaying) {
        if (isAdded()) {
            if (isPlaying) {
                btnPlay.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);
            } else {
                btnPlay.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onProgressUpdate(int currentPosition, int maxDuration) {
        if (isAdded()) {
            seekBar.setMax(maxDuration);
            seekBar.setProgress(currentPosition);

            String currentTimeStr = String.format(Locale.getDefault(), "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(currentPosition),
                    TimeUnit.MILLISECONDS.toSeconds(currentPosition) % 60);
            long remainingTime = maxDuration - currentPosition;
            String remainingTimeStr = String.format(Locale.getDefault(), "-%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(remainingTime),
                    TimeUnit.MILLISECONDS.toSeconds(remainingTime) % 60);

            textCurrentTime.setText(currentTimeStr);
            textRemainingTime.setText(remainingTimeStr);
        }
    }
}