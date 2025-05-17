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

        // Отображение голов
        StringBuilder goalText = new StringBuilder("Голы:\n");

        // Домашняя команда
        goalText.append(match.getHomeTeam()).append(": ");
        List<Match.Goal> homeGoals = match.getHomeGoalDetails();
        Log.d(TAG, "Матч ID " + match.getFixtureId() + ": Домашние голы=" + (homeGoals != null ? homeGoals.size() : "null"));
        if (homeGoals == null || homeGoals.isEmpty()) {
            goalText.append("Нет голов");
            Log.w(TAG, "Нет данных о домашних голах для матча ID " + match.getFixtureId());
        } else {
            StringBuilder homeGoalsText = new StringBuilder();
            for (int i = 0; i < homeGoals.size(); i++) {
                Match.Goal goal = homeGoals.get(i);
                String playerName = goal.getPlayerName() != null ? goal.getPlayerName() : "Неизвестный игрок";
                int minute = goal.getMinute(); // minute is an int, no need for null check
                String goalInfo = playerName + " (" + minute + "')";
                homeGoalsText.append(goalInfo);
                if (i < homeGoals.size() - 1) {
                    homeGoalsText.append(", ");
                }
                Log.d(TAG, "Домашний гол: " + goalInfo);
            }
            goalText.append(homeGoalsText.toString());
        }

        // Гостевая команда
        goalText.append("\n").append(match.getAwayTeam()).append(": ");
        List<Match.Goal> awayGoals = match.getAwayGoalDetails();
        Log.d(TAG, "Матч ID " + match.getFixtureId() + ": Гостевые голы=" + (awayGoals != null ? awayGoals.size() : "null"));
        if (awayGoals == null || awayGoals.isEmpty()) {
            goalText.append("Нет голов");
            Log.w(TAG, "Нет данных о гостевых голах для матча ID " + match.getFixtureId());
        } else {
            StringBuilder awayGoalsText = new StringBuilder();
            for (int i = 0; i < awayGoals.size(); i++) {
                Match.Goal goal = awayGoals.get(i);
                String playerName = goal.getPlayerName() != null ? goal.getPlayerName() : "Неизвестный игрок";
                int minute = goal.getMinute(); // minute is an int, no need for null check
                String goalInfo = playerName + " (" + minute + "')";
                awayGoalsText.append(goalInfo);
                if (i < awayGoals.size() - 1) {
                    awayGoalsText.append(", ");
                }
                Log.d(TAG, "Гостевой гол: " + goalInfo);
            }
            goalText.append(awayGoalsText.toString());
        }
        holder.goalScorers.setText(goalText.toString());

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
        TextView matchDate, matchInfo, goalScorers;
        ImageView homeTeamLogo, awayTeamLogo;
        Button btnLineups, btnStats, btnChat, btnPredict;

        public ViewHolder(View itemView) {
            super(itemView);
            matchDate = itemView.findViewById(R.id.match_date);
            matchInfo = itemView.findViewById(R.id.match_info);
            goalScorers = itemView.findViewById(R.id.goal_scorers);
            homeTeamLogo = itemView.findViewById(R.id.home_team_logo);
            awayTeamLogo = itemView.findViewById(R.id.away_team_logo);
            btnLineups = itemView.findViewById(R.id.btn_lineups);
            btnStats = itemView.findViewById(R.id.btn_stats);
            btnChat = itemView.findViewById(R.id.btn_chat);
            btnPredict = itemView.findViewById(R.id.btn_predict);
        }
    }
}