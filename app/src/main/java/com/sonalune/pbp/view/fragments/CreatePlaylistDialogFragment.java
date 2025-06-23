package com.sonalune.pbp.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.sonalune.pbp.R;

public class CreatePlaylistDialogFragment extends DialogFragment {

    private EditText etPlaylistName;
    private Button btnCreate, btnCancel;

    public interface OnPlaylistCreatedListener {
        void onPlaylistCreated(String playlistName);
    }

    private OnPlaylistCreatedListener listener;

    public static CreatePlaylistDialogFragment newInstance(OnPlaylistCreatedListener listener) {
        CreatePlaylistDialogFragment fragment = new CreatePlaylistDialogFragment();
        fragment.listener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_playlist_dialog, container, false);

        etPlaylistName = view.findViewById(R.id.etPlaylistName);
        btnCreate = view.findViewById(R.id.btnCreate);
        btnCancel = view.findViewById(R.id.btnCancel);

        btnCreate.setOnClickListener(v -> {
            String playlistName = etPlaylistName.getText().toString().trim();
            if (!playlistName.isEmpty()) {
                if (listener != null) {
                    listener.onPlaylistCreated(playlistName);
                }
                dismiss();
            } else {
                etPlaylistName.setError("Nama tidak boleh kosong");
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());

        return view;
    }
}