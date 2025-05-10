package com.example.footzone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example

        .footzone.R;
import com.example.footzone.model.Transfer;
import java.util.List;

public class TransferAdapter extends RecyclerView.Adapter<TransferAdapter.TransferViewHolder> {
    private List<Transfer> transfers;

    public TransferAdapter(List<Transfer> transfers) {
        this.transfers = transfers;
    }

    @Override
    public TransferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transfer, parent, false);
        return new TransferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransferViewHolder holder, int position) {
        Transfer transfer = transfers.get(position);
        holder.playerName.setText(transfer.getPlayerName());
        holder.fromTeam.setText("From: " + transfer.getFromTeam());
        holder.toTeam.setText("To: " + transfer.getToTeam());
        holder.date.setText("Date: " + transfer.getDate());
        holder.fee.setText("Fee: " + transfer.getFee());
    }

    @Override
    public int getItemCount() {
        return transfers.size();
    }

    static class TransferViewHolder extends RecyclerView.ViewHolder {
        TextView playerName, fromTeam, toTeam, date, fee;

        TransferViewHolder(View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.transfer_player_name);
            fromTeam = itemView.findViewById(R.id.transfer_from_team);
            toTeam = itemView.findViewById(R.id.transfer_to_team);
            date = itemView.findViewById(R.id.transfer_date);
            fee = itemView.findViewById(R.id.transfer_fee);
        }
    }
}