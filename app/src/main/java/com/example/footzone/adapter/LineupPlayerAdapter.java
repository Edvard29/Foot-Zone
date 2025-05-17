package com.example.footzone.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.footzone.R;
import com.example.footzone.model.Player;
import java.util.ArrayList;
import java.util.List;

public class LineupPlayerAdapter extends RecyclerView.Adapter<LineupPlayerAdapter.PlayerViewHolder> {
    private static final String TAG = "LineupPlayerAdapter";
    private List<Player> players;

    public LineupPlayerAdapter(List<Player> players) {
        this.players = players != null ? players : new ArrayList<>();
        Log.d(TAG, "Adapter initialized with " + this.players.size() + " players");
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lineup_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = players.get(position);
        if (player != null) {
            String name = player.getName() != null ? player.getName() : "Unknown";
            String playerPosition = player.getPosition() != null ? player.getPosition() : "N/A";
            String ageText = player.getAge() > 0 ? "Age: " + player.getAge() : "Age: N/A";
            String photoUrl = player.getPhotoUrl();

            holder.playerName.setText(name);
            holder.playerPosition.setText(playerPosition);
            holder.playerAge.setText(ageText);

            Log.d(TAG, "Binding player: " + name + ", PhotoUrl: " + photoUrl);
            if (photoUrl != null && !photoUrl.trim().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(photoUrl)
                        .placeholder(R.drawable.ic_default_player_image)
                        .error(R.drawable.ic_default_player_image)
                        .circleCrop()
                        .into(holder.playerImage);
            } else {
                Log.w(TAG, "No valid photoUrl for player: " + name);
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.ic_default_player_image)
                        .circleCrop()
                        .into(holder.playerImage);
            }
        } else {
            Log.w(TAG, "Player at position " + position + " is null");
            holder.playerName.setText("Unknown");
            holder.playerPosition.setText("N/A");
            holder.playerAge.setText("Age: N/A");
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.ic_default_player_image)
                    .circleCrop()
                    .into(holder.playerImage);
        }
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        ImageView playerImage;
        TextView playerName;
        TextView playerPosition;
        TextView playerAge;

        PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerImage = itemView.findViewById(R.id.player_image);
            playerName = itemView.findViewById(R.id.player_name);
            playerPosition = itemView.findViewById(R.id.player_position);
            playerAge = itemView.findViewById(R.id.player_age);
            Log.d(TAG, "ViewHolder initialized, imageView: " + (playerImage != null) + ", nameView: " + (playerName != null));
        }
    }
}