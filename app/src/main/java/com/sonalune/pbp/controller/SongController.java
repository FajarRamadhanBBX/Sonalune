package com.sonalune.pbp.controller;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

public class SongController {
    private MediaPlayer mediaPlayer;
    private Context context;
    private static final String TAG = "SongController";

    public SongController(Context context) {
        this.context = context;
    }

    public void playSong(String songUrl) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(songUrl);
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                Toast.makeText(context, "Playing", Toast.LENGTH_SHORT).show();
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                Toast.makeText(context, "Playback finished", Toast.LENGTH_SHORT).show();
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "Error playing song", e);
            Toast.makeText(context, "Error playing song", Toast.LENGTH_SHORT).show();
        }
    }

    public void pauseSong() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Toast.makeText(context, "Paused", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            Toast.makeText(context, "Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}