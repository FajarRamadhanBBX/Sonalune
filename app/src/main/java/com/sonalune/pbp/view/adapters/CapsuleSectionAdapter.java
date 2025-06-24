package com.sonalune.pbp.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.sonalune.pbp.R;
import com.sonalune.pbp.model.Singer;
import com.sonalune.pbp.model.Song;
import com.sonalune.pbp.model.User;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CapsuleSectionAdapter extends RecyclerView.Adapter<CapsuleSectionAdapter.ViewHolder> {

    private final Context context;
    private User user;
    private List<Song> topSongs = new ArrayList<>();
    private List<Singer> topArtists = new ArrayList<>();

    private static final int VIEW_TYPE_ALL_TIME = 0;
    private static final int VIEW_TYPE_LAST_MONTH = 1;

    public CapsuleSectionAdapter(Context context) {
        this.context = context;
    }

    public void setData(User user, List<Song> topSongs, List<Singer> topArtists) {
        this.user = user;
        this.topSongs = topSongs != null ? topSongs : new ArrayList<>();
        this.topArtists = topArtists != null ? topArtists : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ui_item_capsule_section, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ALL_TIME) {
            holder.bindAllTime(user, topSongs, topArtists);
        } else if (getItemViewType(position) == VIEW_TYPE_LAST_MONTH) {
            holder.bindLastMonth(user, topSongs, topArtists);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_ALL_TIME : VIEW_TYPE_LAST_MONTH;
    }

    @Override
    public int getItemCount() {
        return user != null ? 2 : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView periodTitle, musicMinutes;
        RecyclerView rvTopSongs, rvTopArtists;

        ViewHolder(View itemView) {
            super(itemView);
            periodTitle = itemView.findViewById(R.id.timeCapsuled);
            musicMinutes = itemView.findViewById(R.id.minuteSong);
            rvTopSongs = itemView.findViewById(R.id.recyclerViewTopSong);
            rvTopArtists = itemView.findViewById(R.id.recyclerViewTopSinger);
        }

        void bindAllTime(User userData, List<Song> songs, List<Singer> artists) {
            if (userData == null) return;
            periodTitle.setText("ALL TIME");
            long totalMinutes = (userData.getTotalListenSeconds() > 0) ? userData.getTotalListenSeconds() / 60 : 0;
            String formattedMinutes = NumberFormat.getInstance(Locale.US).format(totalMinutes) + " Minutes";
            musicMinutes.setText(formattedMinutes);
            setupAdapters(songs, artists);
        }

        void bindLastMonth(User userData, List<Song> songs, List<Singer> artists) {
            if (userData == null) return;
            periodTitle.setText("LAST MONTH");
            long monthlyMinutes = (userData.getMonthlyListenSeconds() > 0) ? userData.getMonthlyListenSeconds() / 60 : 0;
            String formattedMinutes = NumberFormat.getInstance(Locale.US).format(monthlyMinutes) + " Minutes";
            musicMinutes.setText(formattedMinutes);
            setupAdapters(songs, artists);
        }

        private void setupAdapters(List<Song> songs, List<Singer> artists) {
            rvTopSongs.setLayoutManager(new LinearLayoutManager(context));
            rvTopSongs.setAdapter(new TopSongAdapter(songs, artists));

            rvTopArtists.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            rvTopArtists.setAdapter(new TopSingerAdapter(artists));
        }
    }
}