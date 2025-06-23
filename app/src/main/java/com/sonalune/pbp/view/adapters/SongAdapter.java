package com.sonalune.pbp.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sonalune.pbp.R;
import com.sonalune.pbp.model.Song;
import com.sonalune.pbp.model.Singer;
import com.bumptech.glide.Glide;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<Song> songs;
    private List<Singer> singers;
    private OnItemClickListener listener;

    public SongAdapter(List<Song> songs) {
        this.songs = songs;
    }
    public interface OnItemClickListener {
        void onItemClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public SongAdapter(List<Song> songs, List<Singer> singers) {
        this.songs = songs;
        this.singers = singers;
    }

    // ViewHolder = class untuk mengelola tampilan 1 item
    public class SongViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvArtist;
        ImageView imageSong;

        public SongViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.txt_title);
            tvArtist = itemView.findViewById(R.id.txt_artist);
            imageSong = itemView.findViewById(R.id.img_song);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
            }
        });
        }

        

        public void bind(Song song, Singer singer) {
            tvTitle.setText(song.getTitle());
            tvArtist.setText(singer != null ? singer.getName() : "-");
            Glide.with(itemView.getContext()).load(song.getImageUrl()).into(imageSong);
        }
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ui_item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        Song song = songs.get(position);
        Singer singer = null;
        // Cari singer yang id-nya sama dengan singerId di song
        if (singers != null) {
            for (Singer s : singers) {
                if (s.getId() != null && s.getId().equals(song.getSingerId())) {
                    singer = s;
                    break;
                }
            }
        }
        holder.bind(song, singer);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }
}
