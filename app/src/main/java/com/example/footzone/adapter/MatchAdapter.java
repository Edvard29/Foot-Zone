package com.example.footzone.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.footzone.R;
import com.example.footzone.model.Match;
import java.util.List;



    public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {
        private List<Match> matchList;

        public MatchAdapter(List<Match> matchList) {
            this.matchList = matchList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Match match = matchList.get(position);
            holder.matchDate.setText(match.getDate().substring(0, 10)); // Показывает только дату
            holder.matchInfo.setText(match.getHomeTeam() + " " + match.getHomeGoals() + " - " + match.getAwayGoals() + " " + match.getAwayTeam());
            holder.matchInfo.setTextSize(18);
            holder.matchInfo.setTextColor(0xFF004D40); // Dark green color
        }

        @Override
        public int getItemCount() {
            return matchList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView matchInfo;


            TextView matchDate;

            public ViewHolder(View itemView) {
                super(itemView);
                matchDate = itemView.findViewById(R.id.match_date);
                matchInfo = itemView.findViewById(R.id.match_info);
                itemView.setBackgroundColor(0xFFA5D6A7); // Light green background
                matchInfo.setPadding(20, 20, 20, 20);
                matchInfo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }

        }
    }
