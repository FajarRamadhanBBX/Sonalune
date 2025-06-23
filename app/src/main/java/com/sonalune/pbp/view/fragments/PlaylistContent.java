package com.sonalune.pbp.view.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.sonalune.pbp.R;
import com.sonalune.pbp.model.Singer;
import com.sonalune.pbp.model.Song;
import com.sonalune.pbp.view.adapters.AddToPlaylistDialogFragment;
import com.sonalune.pbp.view.adapters.SongAdapter;

import java.util.ArrayList;
import java.util.List;
import com.sonalune.pbp.controller.PlaylistController;

public class PlaylistContent extends Fragment implements CreatePlaylistDialogFragment.OnPlaylistCreatedListener {

    private RecyclerView recyclerViewSong;
    private Song songForAction;
    private ImageView imagePlaylistContent, backButton;
    private OnSongSelectedListener songSelectedListener;
    private List<Song> playlistSong = new ArrayList<>();
    private List<Singer> singerList = new ArrayList<>();
    private SongAdapter songAdapter;
    private String playlistId;
    private String imagePlaylistUrl;
    private PlaylistController playlistController;

    public interface OnSongSelectedListener {
        void onSongSelected(List<Song> songList, int position, List<Singer> singerList);
    }

    public static PlaylistContent newInstance(String playlistId, String imageUrl) {
        PlaylistContent fragment = new PlaylistContent();
        Bundle args = new Bundle();
        args.putString("id", playlistId);
        args.putString("imageUrl", imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaylistContent() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSongSelectedListener) {
            songSelectedListener = (OnSongSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSongSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_content, container, false);
        playlistController = new PlaylistController();
        initViews(view);
        initAdapter();
        setupListeners();
        loadPlaylistData();
        return view;
    }

    private void initViews(View view) {
        recyclerViewSong = view.findViewById(R.id.recyclerViewSong);
        imagePlaylistContent = view.findViewById(R.id.img_playlist_content);
        backButton = view.findViewById(R.id.backButtonPlaylist);

        recyclerViewSong.setLayoutManager(new LinearLayoutManager(getContext()));

        backButton.setOnClickListener(v -> {
            if (isAdded()) requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void initAdapter() {
        songAdapter = new SongAdapter(playlistSong, singerList);
        recyclerViewSong.setAdapter(songAdapter);
    }

    private void setupListeners() {
        if (songAdapter == null) return;

        songAdapter.setOnItemClickListener(position -> {
            if (songSelectedListener != null) {
                songSelectedListener.onSongSelected(playlistSong, position, singerList);
            }
        });

        songAdapter.setOnMoreOptionsClickListener((view, song) -> {
            showPopupMenu(view, song);
        });
    }

    private void showPopupMenu(View view, Song song) {
        this.songForAction = song;
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater().inflate(R.menu.song_option_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_remove_from_playlist) {
                removeSongFromPlaylist(song);
                return true;
            } else if (itemId == R.id.action_add_to_playlist) {
                showAddToPlaylistDialog();
                Toast.makeText(getContext(), "Fitur 'Tambah ke playlist' belum diimplementasikan.", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.action_create_playlist) {
                showCreatePlaylistDialog();
                Toast.makeText(getContext(), "Fitur 'Buat playlist' belum diimplementasikan.", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showCreatePlaylistDialog() {
        CreatePlaylistDialogFragment dialog = CreatePlaylistDialogFragment.newInstance(this);
        dialog.show(getParentFragmentManager(), "CreatePlaylistDialog");
    }

    private void showAddToPlaylistDialog() {
        AddToPlaylistDialogFragment dialog = AddToPlaylistDialogFragment.newInstance(playlistId, selectedPlaylistId -> {
            addSongToPlaylist(selectedPlaylistId, songForAction);
        });
        dialog.show(getParentFragmentManager(), "AddToPlaylistDialog");
    }

    @Override
    public void onPlaylistCreated(String playlistName) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId == null || songForAction == null) return;

        playlistController.createPlaylist(userId, playlistName, songForAction, new PlaylistController.SimpleListener() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addSongToPlaylist(String targetPlaylistId, Song song) {
        if (targetPlaylistId == null || song == null) return;

        playlistController.addSongToPlaylist(targetPlaylistId, song, new PlaylistController.SimpleListener() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeSongFromPlaylist(Song song) {
        if (playlistId == null || song.getId() == null) {
            Toast.makeText(getContext(), "Error: Gagal mendapatkan ID", Toast.LENGTH_SHORT).show();
            return;
        }
        playlistController.removeSongFromPlaylist(playlistId, song, new PlaylistController.SimpleListener() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                int position = playlistSong.indexOf(song);
                if (position != -1) {
                    playlistSong.remove(position);
                    songAdapter.notifyItemRemoved(position);
                    songAdapter.notifyItemRangeChanged(position, playlistSong.size());
                }
            }
            @Override
            public void onFailure(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPlaylistData() {
        this.playlistId = getArguments() != null ? getArguments().getString("id") : null;
        this.imagePlaylistUrl = getArguments() != null ? getArguments().getString("imageUrl") : null;

        if (imagePlaylistUrl != null) {
            Glide.with(this).load(imagePlaylistUrl).into(imagePlaylistContent);
        }
        if (this.playlistId == null) return;

        playlistController.loadPlaylist(playlistId, imagePlaylistUrl, new PlaylistController.PlaylistDataListener() {
            @Override
            public void onPlaylistLoaded(List<Song> songs, List<Singer> singers, String imageUrl) {
                playlistSong.clear();
                playlistSong.addAll(songs);
                singerList.clear();
                singerList.addAll(singers);
                songAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        songSelectedListener = null;
    }
}