package com.example.footzone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.footzone.R;
import com.example.footzone.model.Player;
import java.util.List;

public class LineupPlayerAdapter extends RecyclerView.Adapter<LineupPlayerAdapter.PlayerViewHolder> {
    private List<Player> players;

    public LineupPlayerAdapter(List<Player> players) {
        this.players = players;
    }

    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lineup_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlayerViewHolder holder, int position) {
        Player player = players.get(position);
        holder.playerName.setText(player.getName());
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerName;

        PlayerViewHolder(View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.player_name);
        }
    }
}