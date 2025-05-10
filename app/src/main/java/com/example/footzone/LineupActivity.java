package com.example.footzone;

import android.os.Bundle;

import android.widget.TextView;

public class LineupActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView не нужен, так как вызывается в BaseActivity
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Team Lineups");
        }

        TextView homeTeamName = findViewById(R.id.home_team_name);
        TextView homeTeamLineup = findViewById(R.id.home_team_lineup);
        TextView awayTeamName = findViewById(R.id.away_team_name);
        TextView awayTeamLineup = findViewById(R.id.away_team_lineup);

        // Получение данных из Intent
        String homeTeam = getIntent().getStringExtra("homeTeam");
        String homeLineup = getIntent().getStringExtra("homeLineup");
        String awayTeam = getIntent().getStringExtra("awayTeam");
        String awayLineup = getIntent().getStringExtra("awayLineup");

        // Установка данных
        homeTeamName.setText(homeTeam);
        homeTeamLineup.setText(homeLineup);
        awayTeamName.setText(awayTeam);
        awayTeamLineup.setText(awayLineup);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_lineup;
    }
}