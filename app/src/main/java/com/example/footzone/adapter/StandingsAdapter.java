package com.example.footzone.adapter;


import com.example.footzone.R;
import com.example.footzone.model.TeamStanding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StandingsAdapter extends RecyclerView.Adapter<StandingsAdapter.ViewHolder> {

    private List<TeamStanding> standingsList;

    public StandingsAdapter(List<TeamStanding> standingsList) {
        this.standingsList = standingsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.standings_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TeamStanding standing = standingsList.get(position);
        holder.teamName.setText(standing.getTeamName());
        holder.points.setText(String.valueOf(standing.getPoints()));
    }

    @Override
    public int getItemCount() {
        return standingsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView teamName, points;

        public ViewHolder(View itemView) {
            super(itemView);
            teamName = itemView.findViewById(R.id.team_name);
            points = itemView.findViewById(R.id.team_points);
        }
    }
}