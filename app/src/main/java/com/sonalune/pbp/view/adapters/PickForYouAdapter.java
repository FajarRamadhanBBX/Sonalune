package com.sonalune.pbp.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.sonalune.pbp.R;
import com.sonalune.pbp.model.Song;
import com.bumptech.glide.Glide;

import java.util.List;

public class PickForYouAdapter extends RecyclerView.Adapter<PickForYouAdapter.PickForYouViewHolder> {
    private static List<Song> songs;
    private static OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Song song);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public PickForYouAdapter(List<Song> songs) {
        this.songs = songs;
    }

    public static class PickForYouViewHolder extends RecyclerView.ViewHolder {
        ImageView pickForYouImage;

        public PickForYouViewHolder (View itemView) {
            super(itemView);
            pickForYouImage = itemView.findViewById(R.id.img_pick_for_you);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION){
                    listener.onItemClick(songs.get(position));
                }
            });
        }

        public void bind(Song song) {
            Glide.with(itemView.getContext()).load(song.getImageUrl()).into(pickForYouImage);
        }
    }

    @Override
    public PickForYouViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ui_item_pick_for_you, parent, false);
        return new PickForYouViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PickForYouViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }
}
