/*
 * FootballerAdapter.java
 * RecyclerView adapter for displaying top goal scorers or assist providers with player images.
 * Features:
 * - Displays player name, goals/assists, and image in a MaterialCardView.
 * - Uses Glide for efficient image loading.
 * - Supports both goal scorers and assist providers with conditional layouts.
 *
 * Dependencies:
 * - Glide: implementation 'com.github.bumptech.glide:glide:4.16.0'
 * - Material Components: implementation 'com.google.android.material:material:1.12.0'
 */
package com.example.footzone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.footzone.R;
import com.example.footzone.model.Footballer;
import java.util.List;

public class FootballerAdapter extends RecyclerView.Adapter<FootballerAdapter.ViewHolder> {

    private final List<Footballer> footballers;
    private final boolean isGoalScorerPage;

    public FootballerAdapter(List<Footballer> footballers, boolean isGoalScorerPage) {
        this.footballers = footballers;
        this.isGoalScorerPage = isGoalScorerPage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isGoalScorerPage ? R.layout.item_goal_scorer : R.layout.item_assist_provider;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(view, isGoalScorerPage);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Footballer footballer = footballers.get(position);
        holder.nameTextView.setText(footballer.getName());

        if (isGoalScorerPage) {
            holder.statTextView.setText(footballer.getGoals() + " Goals");
        } else {
            holder.statTextView.setText(footballer.getAssists() + " Assists");
        }

        // Load player image with Glide
        String imageUrl = footballer.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .transform(new CircleCrop())
                    .placeholder(R.drawable.ic_player_placeholder)
                    .error(R.drawable.ic_player_placeholder)
                    .into(holder.playerImageView);
        } else {
            holder.playerImageView.setImageResource(R.drawable.ic_player_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return footballers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView statTextView;
        ImageView playerImageView;

        public ViewHolder(@NonNull View itemView, boolean isGoalScorerPage) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_view_name);
            statTextView = itemView.findViewById(isGoalScorerPage ? R.id.text_view_goals : R.id.text_view_assists);
            playerImageView = itemView.findViewById(R.id.image_view_player);
        }
    }
}