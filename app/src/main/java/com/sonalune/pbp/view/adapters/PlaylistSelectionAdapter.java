package com.sonalune.pbp.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sonalune.pbp.R;
import com.sonalune.pbp.model.Playlist;
import java.util.List;

public class PlaylistSelectionAdapter extends RecyclerView.Adapter<PlaylistSelectionAdapter.ViewHolder> {

    private List<Playlist> playlists;
    private OnPlaylistSelectedListener listener;

    public interface OnPlaylistSelectedListener {
        void onPlaylistSelected(Playlist playlist);
    }

    public PlaylistSelectionAdapter(List<Playlist> playlists, OnPlaylistSelectedListener listener) {
        this.playlists = playlists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ui_item_dialog_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(playlists.get(position));
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlaylistName;

        ViewHolder(View itemView) {
            super(itemView);
            tvPlaylistName = itemView.findViewById(R.id.tvPlaylistName);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlaylistSelected(playlists.get(getAdapterPosition()));
                }
            });
        }

        void bind(Playlist playlist) {
            tvPlaylistName.setText(playlist.getName());
        }
    }
}