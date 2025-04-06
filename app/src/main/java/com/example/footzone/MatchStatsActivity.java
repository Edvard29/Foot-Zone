package com.example.footzone;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MatchStatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_stats);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView matchTeams = findViewById(R.id.match_teams);
        TextView matchDate = findViewById(R.id.match_date);
        TextView possession = findViewById(R.id.possession);
        TextView shotsOnGoal = findViewById(R.id.shots_on_goal);
        TextView shotsOffGoal = findViewById(R.id.shots_off_goal);
        TextView corners = findViewById(R.id.corners);
        TextView fouls = findViewById(R.id.fouls);

        // Получение данных из Intent
        Intent intent = getIntent();
        String homeTeam = intent.getStringExtra("homeTeam");
        String awayTeam = intent.getStringExtra("awayTeam");
        String date = intent.getStringExtra("date");
        String possessionData = intent.getStringExtra("possession");
        String shotsOnGoalData = intent.getStringExtra("shotsOnGoal");
        String shotsOffGoalData = intent.getStringExtra("shotsOffGoal");
        String cornersData = intent.getStringExtra("corners");
        String foulsData = intent.getStringExtra("fouls");

        // Установка данных
        matchTeams.setText(homeTeam + " vs " + awayTeam);
        matchDate.setText(date);
        possession.setText("Possession: " + possessionData);
        shotsOnGoal.setText("Shots on Goal: " + shotsOnGoalData);
        shotsOffGoal.setText("Shots off Goal: " + shotsOffGoalData);
        corners.setText("Corners: " + cornersData);
        fouls.setText("Fouls: " + foulsData);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}