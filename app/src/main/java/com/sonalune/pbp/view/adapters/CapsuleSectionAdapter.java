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
    private List<Song> topSongsAllTime = new ArrayList<>();
    private List<Singer> topArtistsAllTime = new ArrayList<>();
    private List<Song> topSongsMonthly = new ArrayList<>();
    private List<Singer> topArtistsMonthly = new ArrayList<>();

    private static final int VIEW_TYPE_ALL_TIME = 0;
    private static final int VIEW_TYPE_LAST_MONTH = 1;

    public CapsuleSectionAdapter(Context context) {
        this.context = context;
    }

    public void setData(User user, List<Song> topSongsAllTime, List<Singer> topArtistsAllTime, List<Song> topSongsMonthly, List<Singer> topArtistsMonthly) {
        this.user = user;
        this.topSongsAllTime = topSongsAllTime != null ? topSongsAllTime : new ArrayList<>();
        this.topArtistsAllTime = topArtistsAllTime != null ? topArtistsAllTime : new ArrayList<>();
        this.topSongsMonthly = topSongsMonthly != null ? topSongsMonthly : new ArrayList<>();
        this.topArtistsMonthly = topArtistsMonthly != null ? topArtistsMonthly : new ArrayList<>();
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
            holder.bindAllTime(user, topSongsAllTime, topArtistsAllTime);
        } else if (getItemViewType(position) == VIEW_TYPE_LAST_MONTH) {
            holder.bindThisMonth(user, topSongsMonthly, topArtistsMonthly);
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

        void bindThisMonth(User userData, List<Song> songs, List<Singer> artists) {
            if (userData == null) return;
            periodTitle.setText("THIS MONTH");
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