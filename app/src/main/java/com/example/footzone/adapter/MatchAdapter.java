package com.example.footzone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_match, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Match match = matchList.get(position);

        // Показать только дату и команды с результатом
        holder.matchDate.setText(match.getDate().substring(0, 10));  // Дата
        holder.matchDate.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.date_text_color));

        String score = match.getStatus().equals("NS")
                ? "-"
                : match.getHomeGoals() + " : " + match.getAwayGoals();

        holder.matchInfo.setText(match.getHomeTeam() + " " + score + " " + match.getAwayTeam());  // Команды и счёт
        holder.matchInfo.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.team_info_text_color));

        // Применение новых цветов
    }


    @Override
    public int getItemCount() {
        return matchList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView matchDate, matchInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            matchDate = itemView.findViewById(R.id.match_date);
            matchInfo = itemView.findViewById(R.id.match_info);
        }
    }
}
