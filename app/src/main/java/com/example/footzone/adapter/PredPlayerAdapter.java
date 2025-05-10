package com.example.footzone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
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
        holder.checkBox.setChecked(selectedPlayerIds.contains(player.getId()));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listener.onPlayerSelected(player.getId(), isChecked);
            holder.checkBox.setChecked(selectedPlayerIds.contains(player.getId())); // Обновляем состояние
        });
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    static class PredPlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerName;
        CheckBox checkBox;

        PredPlayerViewHolder(View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.player_name);
            checkBox = itemView.findViewById(R.id.player_checkbox);
        }
    }
}