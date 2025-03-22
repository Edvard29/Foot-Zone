package com.example.footzone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footzone.R;
import com.example.footzone.model.TransferItem;

import java.util.List;

public class TransferAdapter extends RecyclerView.Adapter<TransferAdapter.ViewHolder> {
    private List<TransferItem> transferList;

    public TransferAdapter(List<TransferItem> transferList) {
        this.transferList = transferList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transfer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransferItem transfer = transferList.get(position);
        holder.playerName.setText(transfer.getPlayerName());
        holder.fromTeam.setText("Из: " + transfer.getFromTeam());
        holder.toTeam.setText("В: " + transfer.getToTeam());
    }

    @Override
    public int getItemCount() {
        return transferList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView playerName, fromTeam, toTeam;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.text_player_name);
            fromTeam = itemView.findViewById(R.id.text_from_team);
            toTeam = itemView.findViewById(R.id.text_to_team);
        }
    }
}
