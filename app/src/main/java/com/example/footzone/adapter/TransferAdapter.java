package com.example.footzone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.footzone.R;
import com.example.footzone.model.Transfer;

import java.util.List;

public class TransferAdapter extends RecyclerView.Adapter<TransferAdapter.TransferViewHolder> {

    private List<Transfer> transferList;

    public TransferAdapter(List<Transfer> transferList) {
        this.transferList = transferList;
    }

    @Override
    public TransferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transfer, parent, false);
        return new TransferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransferViewHolder holder, int position) {
        Transfer transfer = transferList.get(position);
        holder.playerName.setText(transfer.getPlayerName());
        holder.fromTeam.setText("From: " + transfer.getFromTeam());
        holder.toTeam.setText("To: " + transfer.getToTeam());
        holder.transferDate.setText("Date: " + transfer.getTransferDate());
    }

    @Override
    public int getItemCount() {
        return transferList.size();
    }

    public static class TransferViewHolder extends RecyclerView.ViewHolder {
        TextView playerName, fromTeam, toTeam, transferDate;

        public TransferViewHolder(View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.player_name);
            fromTeam = itemView.findViewById(R.id.from_team);
            toTeam = itemView.findViewById(R.id.to_team);
            transferDate = itemView.findViewById(R.id.transfer_date);
        }
    }
}
