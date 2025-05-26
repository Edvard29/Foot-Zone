package com.example.footzone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class MatchStatsActivity extends BaseActivity {

    private static final String TAG = "MatchStatsActivity";

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_match_stats;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize views
        TextView matchTeams = findViewById(R.id.match_teams);
        TextView matchDate = findViewById(R.id.match_date);
        TextView possession = findViewById(R.id.possession);
        TextView shotsOnGoal = findViewById(R.id.shots_on_goal);
        TextView shotsOffGoal = findViewById(R.id.shots_off_goal);
        TextView corners = findViewById(R.id.corners);
        TextView fouls = findViewById(R.id.fouls);

        // Get data from Intent
        Intent intent = getIntent();
        String homeTeam = intent.getStringExtra("homeTeam");
        String awayTeam = intent.getStringExtra("awayTeam");
        String date = intent.getStringExtra("date");
        String possessionData = intent.getStringExtra("possession");
        String shotsOnGoalData = intent.getStringExtra("shotsOnGoal");
        String shotsOffGoalData = intent.getStringExtra("shotsOffGoal");
        String cornersData = intent.getStringExtra("corners");
        String foulsData = intent.getStringExtra("fouls");

        // Log Intent data for debugging
        Log.d(TAG, "Intent data: homeTeam=" + homeTeam + ", awayTeam=" + awayTeam + ", date=" + date +
                ", possession=" + possessionData + ", shotsOnGoal=" + shotsOnGoalData +
                ", shotsOffGoal=" + shotsOffGoalData + ", corners=" + cornersData + ", fouls=" + foulsData);

        // Set data to views
        if (homeTeam != null && awayTeam != null) {
            matchTeams.setText(homeTeam + " vs " + awayTeam);
        } else {
            matchTeams.setText("Неизвестные команды");
            Log.w(TAG, "Missing team data");
        }

        matchDate.setText(date != null ? date : "Неизвестная дата");
        possession.setText(possessionData != null ? "Владение мячом: " + possessionData : "Владение мячом: Н/Д");
        shotsOnGoal.setText(shotsOnGoalData != null ? "Удары в створ: " + shotsOnGoalData : "Удары в створ: Н/Д");
        shotsOffGoal.setText(shotsOffGoalData != null ? "Удары мимо: " + shotsOffGoalData : "Удары мимо: Н/Д");
        corners.setText(cornersData != null ? "Угловые: " + cornersData : "Угловые: Н/Д");
        fouls.setText(foulsData != null ? "Фолы: " + foulsData : "Фолы: Н/Д");

        // Set toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Статистика матча");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}