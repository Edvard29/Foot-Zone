package com.example.footzone.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.footzone.R;
import com.example.footzone.model.Match;
import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {

    private List<Match> matchList;
    private OnMatchActionListener onMatchActionListener;

    public interface OnMatchActionListener {
        void onShowLineups(Match match);
        void onShowStats(Match match);
        void onShowChat(Match match);
        void onShowPrediction(Match match);
    }

    public MatchAdapter(List<Match> matchList, OnMatchActionListener listener) {
        this.matchList = matchList;
        this.onMatchActionListener = listener;
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
        String TAG = "MatchAdapter";

        Log.d(TAG, "Home Team Logo URL: " + match.getHomeTeamLogoUrl());
        Log.d(TAG, "Away Team Logo URL: " + match.getAwayTeamLogoUrl());

        // Установка даты матча
        holder.matchDate.setText(match.getDate().substring(0, 10));
        holder.matchDate.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.date_text_color));

        // Установка информации о матче
        String score = match.getFormattedScore();
        holder.matchInfo.setText(match.getHomeTeam() + " " + score + " " + match.getAwayTeam());
        holder.matchInfo.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.team_info_text_color));

        // Загрузка логотипов команд
        String homeLogoUrl = match.getHomeTeamLogoUrl() != null && match.getHomeTeamLogoUrl().startsWith("http")
                ? match.getHomeTeamLogoUrl()
                : null;
        Glide.with(holder.itemView.getContext())
                .load(homeLogoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_default_team_logo)
                .error(R.drawable.ic_default_team_logo)
                .into(holder.homeTeamLogo);

        String awayLogoUrl = match.getAwayTeamLogoUrl() != null && match.getAwayTeamLogoUrl().startsWith("http")
                ? match.getAwayTeamLogoUrl()
                : null;
        Glide.with(holder.itemView.getContext())
                .load(awayLogoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_default_team_logo)
                .error(R.drawable.ic_default_team_logo)
                .into(holder.awayTeamLogo);


        // Обработчики кнопок
        holder.btnLineups.setOnClickListener(v -> {
            if (onMatchActionListener != null) {
                onMatchActionListener.onShowLineups(match);
            }
        });

        holder.btnStats.setOnClickListener(v -> {
            if (onMatchActionListener != null) {
                onMatchActionListener.onShowStats(match);
            }
        });

        holder.btnChat.setOnClickListener(v -> {
            if (onMatchActionListener != null) {
                onMatchActionListener.onShowChat(match);
            }
        });

        holder.btnPredict.setOnClickListener(v -> {
            if (onMatchActionListener != null) {
                onMatchActionListener.onShowPrediction(match);
            }
        });
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView matchDate, matchInfo;
        ImageView homeTeamLogo, awayTeamLogo;
        Button btnLineups, btnStats, btnChat, btnPredict;

        public ViewHolder(View itemView) {
            super(itemView);
            matchDate = itemView.findViewById(R.id.match_date);
            matchInfo = itemView.findViewById(R.id.match_info);
            homeTeamLogo = itemView.findViewById(R.id.home_team_logo); // ID должен совпадать с XML
            awayTeamLogo = itemView.findViewById(R.id.away_team_logo); // ID должен совпадать с XML
            btnLineups = itemView.findViewById(R.id.btn_lineups);
            btnStats = itemView.findViewById(R.id.btn_stats);
            btnChat = itemView.findViewById(R.id.btn_chat);
            btnPredict = itemView.findViewById(R.id.btn_predict);
        }
    }
}