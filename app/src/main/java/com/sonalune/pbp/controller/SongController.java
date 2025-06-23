package com.sonalune.pbp.controller;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Looper;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.sonalune.pbp.model.Song;

import java.util.List;

public class SongController {
    private MediaPlayer mediaPlayer;
    private Context context;
    private static final String TAG = "SongController";
    private List<Song> playlistSong;
    private int currentSongIndex = -1;
    private SongStateListener stateListener;
    private Handler progressHandler = new Handler(Looper.getMainLooper());
    private Runnable progressUpdater;

    public interface SongStateListener {
        void onSongChanged(Song newSong);
        void onPlaybackStateChanged(boolean isPlaying);
        void onProgressUpdate(int currentPosition, int maxDuration);

    }

    public SongController(Context context) {

        this.context = context;
        initializeProgressUpdater();
    }

    private void initializeProgressUpdater(){
        progressUpdater = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int totalDuration = mediaPlayer.getDuration();
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    if (stateListener != null && totalDuration>0) {
                        stateListener.onProgressUpdate(currentPosition, totalDuration);
                    }
                    progressHandler.postDelayed(this, 1000);
                }
            }
        };
    }

    public void setSongStateListener(SongStateListener listener) {
        this.stateListener = listener;
    }

    public void setPlaylist(List<Song> songs, int startIndex) {
        this.playlistSong = songs;
        this.currentSongIndex = startIndex;
        playCurrentSong();
    }

    public void playCurrentSong() {
        if (playlistSong == null || playlistSong.isEmpty() || currentSongIndex<0 || currentSongIndex >= playlistSong.size()) {
            return;
        }

        progressHandler.removeCallbacks(progressUpdater);

        Song songToPlay = playlistSong.get(currentSongIndex);
        String songUrl = songToPlay.getSongUrl();

        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songUrl);
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                if (stateListener != null){
                    stateListener.onPlaybackStateChanged(true);
                }
                progressHandler.post(progressUpdater);
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                progressHandler.removeCallbacks(progressUpdater);
                playNextSong();
            });
            mediaPlayer.prepareAsync();

            if (stateListener != null) {
                stateListener.onSongChanged(songToPlay);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing song", e);
            Toast.makeText(context, "Error playing song", Toast.LENGTH_SHORT).show();
        }
    }

    public void playNextSong() {
        if (playlistSong == null || playlistSong.isEmpty()) {
            return;
        }
        currentSongIndex++;
        if (currentSongIndex >= playlistSong.size()){
            currentSongIndex = 0;
        }
        playCurrentSong();
    }

    public void playPreviousSong() {
        if (playlistSong == null || playlistSong.isEmpty()) {
            return;
        }
        currentSongIndex--;
        if (currentSongIndex == -1){
            currentSongIndex = 0;
        }
        playCurrentSong();
    }

    public void pauseSong() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            if (stateListener != null) {
                stateListener.onPlaybackStateChanged(false);
            }
            Toast.makeText(context, "Paused", Toast.LENGTH_SHORT).show();
            progressHandler.removeCallbacks(progressUpdater);
        }
    }

    public void resumeSong() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            if (stateListener != null) {
                stateListener.onPlaybackStateChanged(true);
            }
            Toast.makeText(context, "Resumed", Toast.LENGTH_SHORT).show();
            progressHandler.post(progressUpdater);
        }
    }

    public void seekTo(int positionInMillis) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(positionInMillis);
        }
    }

    public boolean isPlaylistSet() {
        return playlistSong != null && !playlistSong.isEmpty();
    }

    public void requestUpdate() {
        if (playlistSong != null && !playlistSong.isEmpty()) {
            stateListener.onSongChanged(playlistSong.get(currentSongIndex));
            if (mediaPlayer != null) {
                stateListener.onPlaybackStateChanged(mediaPlayer.isPlaying());
                stateListener.onProgressUpdate(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
            }
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
            progressHandler.removeCallbacks(progressUpdater);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}