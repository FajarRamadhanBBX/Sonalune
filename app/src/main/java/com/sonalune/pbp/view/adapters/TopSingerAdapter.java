package com.sonalune.pbp.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.sonalune.pbp.R;
import com.sonalune.pbp.model.Singer;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class TopSingerAdapter extends RecyclerView.Adapter<TopSingerAdapter.ViewHolder> {
    private List<Singer> artists;

    public TopSingerAdapter(List<Singer> artists) {
        this.artists = artists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ui_item_top_singer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(artists.get(position));
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView artistImage;
        TextView artistName;

        ViewHolder(View itemView) {
            super(itemView);
            artistImage = itemView.findViewById(R.id.imageTopSinger);
            artistName = itemView.findViewById(R.id.nameTopSinger);
        }

        void bind(Singer artist) {
            artistName.setText(artist.getName());
            Glide.with(itemView.getContext())
                    .load(artist.getImageUrl())
                    .placeholder(R.drawable.im_avatar_photo_profile) // Gambar default
                    .error(R.drawable.im_avatar_photo_profile)
                    .into(artistImage);
        }
    }
}