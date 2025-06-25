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

    private OnMoreOptionsClickListener moreOptionsListener;
    private boolean isPublicPlaylist;
    public interface OnItemClickListener {
        void onItemClick(int position);

    }

    public interface OnMoreOptionsClickListener {
        void onMoreOptionsClick(View view, Song song);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public void setOnMoreOptionsClickListener(OnMoreOptionsClickListener listener) {
        this.moreOptionsListener = listener;
    }
    public SongAdapter(List<Song> songs, List<Singer> singers, boolean isPublic) {
        this.songs = songs;
        this.singers = singers;
        this.isPublicPlaylist = isPublic;
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        TextView titleSong, singerName;
        ImageView imageSong, imageMore;

        public SongViewHolder(View itemView) {
            super(itemView);
            titleSong = itemView.findViewById(R.id.titleSong);
            singerName = itemView.findViewById(R.id.singerName);
            imageSong = itemView.findViewById(R.id.imgSong);
            imageMore = itemView.findViewById(R.id.imageMore);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            });
            imageMore.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (moreOptionsListener != null && position != RecyclerView.NO_POSITION) {
                    moreOptionsListener.onMoreOptionsClick(v, songs.get(position));
                }
            });
        }

        public void bind(Song song, Singer singer) {
            titleSong.setText(song.getTitle());
            singerName.setText(singer != null ? singer.getName() : "-");
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
