package com.sonalune.pbp.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.sonalune.pbp.R;
import com.sonalune.pbp.model.Singer;
import com.sonalune.pbp.model.Song;
import java.util.List;

public class TopSongAdapter extends RecyclerView.Adapter<TopSongAdapter.ViewHolder> {
    private List<Song> songs;
    private List<Singer> allArtists;

    public TopSongAdapter(List<Song> songs, List<Singer> allArtists) {
        this.songs = songs;
        this.allArtists = allArtists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ui_item_top_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(songs.get(position));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, artist;
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTopSong);
            artist = itemView.findViewById(R.id.singerTopSong);
            image = itemView.findViewById(R.id.imageTopSong);
        }

        void bind(Song song) {
            title.setText(song.getTitle());

            // Cari nama artis berdasarkan singerId
            String singerName = "Unknown Artist";
            if (allArtists != null && song.getSingerId() != null) {
                for (Singer singer : allArtists) {
                    if (song.getSingerId().equals(singer.getId())) {
                        singerName = singer.getName();
                        break;
                    }
                }
            }
            artist.setText(singerName);

            Glide.with(itemView.getContext()).load(song.getImageUrl()).into(image);
        }
    }
}