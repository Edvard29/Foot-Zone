package com.example.footzone.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.footzone.R;
import com.example.footzone.model.Team;
import java.util.List;

public class TeamSelectionAdapter extends RecyclerView.Adapter<TeamSelectionAdapter.TeamViewHolder> {
    private static final String TAG = "TeamSelectionAdapter";
    private List<Team> teams;

    public TeamSelectionAdapter(List<Team> teams) {
        this.teams = teams;
        Log.d(TAG, "Adapter initialized with " + teams.size() + " teams");
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_team_selection, parent, false);
        return new TeamViewHolder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        Team team = teams.get(position);
        holder.teamName.setText(team.getName());

        Glide.with(holder.itemView.getContext())
                .load(team.getLogoUrl())
                .placeholder(R.drawable.ic_default_team_logo)
                .error(R.drawable.ic_default_team_logo)
                .circleCrop();

        holder.checkBox.setChecked(team.isSelected());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            team.setSelected(isChecked);
            Log.d(TAG, "Team " + team.getName() + " selected: " + isChecked);
        });

        holder.itemView.setOnClickListener(v -> {
            holder.checkBox.setChecked(!holder.checkBox.isChecked());
        });
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    static class TeamViewHolder extends RecyclerView.ViewHolder {
        TextView teamName;
        CheckBox checkBox;

        TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            teamName = itemView.findViewById(R.id.team_name);
            checkBox = itemView.findViewById(R.id.team_checkbox);
        }
    }
}