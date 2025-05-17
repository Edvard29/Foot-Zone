package com.example.footzone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.footzone.R;
import com.example.footzone.model.PredPlayer;
import java.util.List;

public class PredPlayerAdapter extends RecyclerView.Adapter<PredPlayerAdapter.PredPlayerViewHolder> {
    private List<PredPlayer> players;
    private List<Integer> selectedPlayerIds;
    private OnPlayerSelectionListener listener;

    public interface OnPlayerSelectionListener {
        void onPlayerSelected(int playerId, boolean isSelected);
    }

    public PredPlayerAdapter(List<PredPlayer> players, List<Integer> selectedPlayerIds, OnPlayerSelectionListener listener) {
        this.players = players;
        this.selectedPlayerIds = selectedPlayerIds;
        this.listener = listener;
    }

    @Override
    public PredPlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pred_player, parent, false);
        return new PredPlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PredPlayerViewHolder holder, int position) {
        PredPlayer player = players.get(position);
        holder.playerName.setText(player.getName());
        holder.playerPosition.setText(player.getPosition());
        holder.checkBox.setChecked(selectedPlayerIds.contains(player.getId()));

        // Load player image using Glide
        Glide.with(holder.itemView.getContext())
                .load(player.getImageUrl())
                .placeholder(R.drawable.ic_default_player_image)
                .error(R.drawable.ic_default_player_image)
                .into(holder.playerImage);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listener.onPlayerSelected(player.getId(), isChecked);
            holder.checkBox.setChecked(selectedPlayerIds.contains(player.getId()));
        });
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    static class PredPlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerName;
        TextView playerPosition;
        CheckBox checkBox;
        ImageView playerImage;

        PredPlayerViewHolder(View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.player_name);
            playerPosition = itemView.findViewById(R.id.player_position);
            checkBox = itemView.findViewById(R.id.player_checkbox);
            playerImage = itemView.findViewById(R.id.player_image);
        }
    }
}