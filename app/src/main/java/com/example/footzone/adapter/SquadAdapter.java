package com.example.footzone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

        // Загрузка изображения игрока
        String photoUrl = player.getPhotoUrl() != null && player.getPhotoUrl().startsWith("http")
                ? player.getPhotoUrl()
                : null;
        Glide.with(holder.itemView.getContext())
                .load(photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_default_player_image)
                .error(R.drawable.ic_default_player_image)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, position, age;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.player_name);
            position = itemView.findViewById(R.id.player_position);
            age = itemView.findViewById(R.id.player_age);
            image = itemView.findViewById(R.id.player_image);
        }
    }
}