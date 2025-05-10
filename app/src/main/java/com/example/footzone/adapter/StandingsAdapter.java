package com.example.footzone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.footzone.R;
import com.example.footzone.model.TeamStanding;
import java.util.ArrayList;

public class StandingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private ArrayList<TeamStanding> standings;

    public StandingsAdapter(ArrayList<TeamStanding> standings) {
        this.standings = standings;
    }

    @Override
    public int getItemViewType(int position) {
        return standings.get(position).isHeader() ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_standing_team, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TeamStanding standing = standings.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).title.setText(standing.getName());
        } else if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.name.setText(standing.getName());
            itemHolder.points.setText(standing.getPoints() + " очков");
            itemHolder.stats.setText("С:" + standing.getPlayed() + " В:" + standing.getWins() + " П:" + standing.getLosses());

            // Загрузка логотипа
            String logoUrl = standing.getLogoUrl() != null && standing.getLogoUrl().startsWith("http")
                    ? standing.getLogoUrl()
                    : null;
            Glide.with(itemHolder.itemView.getContext())
                    .load(logoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_default_team_logo)
                    .error(R.drawable.ic_default_team_logo)
                    .into(itemHolder.logo);
        }
    }

    @Override
    public int getItemCount() {
        return standings.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        HeaderViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, points, stats;
        ImageView logo;

        ItemViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.team_name);
            points = itemView.findViewById(R.id.team_points);
            stats = itemView.findViewById(R.id.team_stats);
            logo = itemView.findViewById(R.id.team_logo);
        }
    }
}