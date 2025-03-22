package com.example.footzone.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footzone.R;
import com.example.footzone.model.Player;

import java.util.List;

public class SquadAdapter extends RecyclerView.Adapter<SquadAdapter.ViewHolder> {
    private List<Player> playerList;

    public SquadAdapter(List<Player> playerList) {
        this.playerList = playerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Player player = playerList.get(position);
        holder.name.setText(player.getName());
        holder.position.setText(player.getPosition());
        holder.age.setText(String.valueOf(player.getAge()));
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, position, age;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.player_name);
            position = itemView.findViewById(R.id.player_position);
            age = itemView.findViewById(R.id.player_age);
        }
    }
}
