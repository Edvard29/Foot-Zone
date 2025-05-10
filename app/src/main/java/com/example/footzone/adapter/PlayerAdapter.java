package com.example.footzone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.footzone.R;

import java.util.ArrayList;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private List<String> players;
    private List<String> selectedPlayers;
    private int maxSelections = 11;

    public PlayerAdapter(List<String> players) {
        this.players = players;
        this.selectedPlayers = new ArrayList<>();
    }

    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_players_pred, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlayerViewHolder holder, int position) {
        String player = players.get(position);
        holder.playerName.setText(player);
        holder.checkBox.setChecked(selectedPlayers.contains(player));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (selectedPlayers.size() < maxSelections) {
                    selectedPlayers.add(player);
                } else {
                    holder.checkBox.setChecked(false);
                    // Можно показать Toast: "Максимум 11 игроков"
                }
            } else {
                selectedPlayers.remove(player);
            }
        });
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public List<String> getSelectedPlayers() {
        return selectedPlayers;
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerName;
        CheckBox checkBox;

        PlayerViewHolder(View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.player_name);
            checkBox = itemView.findViewById(R.id.player_checkbox);
        }
    }
}