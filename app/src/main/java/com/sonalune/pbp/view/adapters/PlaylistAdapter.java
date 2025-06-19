package com.sonalune.pbp.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sonalune.pbp.R;
import com.sonalune.pbp.model.Playlist;
import com.bumptech.glide.Glide;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>{
    private List<Playlist> playlists;
    private OnItemClickListener listener;


    public PlaylistAdapter(List<Playlist> playlists) {
        this.playlists = playlists;
    }
    public interface OnItemClickListener {
        void onItemClick(Playlist playlist);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView playlistName;
        ImageView playlistImage;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            playlistImage = itemView.findViewById(R.id.img_playlist);
            playlistName = itemView.findViewById(R.id.name_playlist);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(playlists.get(position));
                }
            });
        }

        public void bind(Playlist playlist) {
            playlistName.setText(playlist.getName());
            Glide.with(itemView.getContext()).load(playlist.getImageUrl()).into(playlistImage);
        }
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ui_item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.bind(playlist);
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }
}
