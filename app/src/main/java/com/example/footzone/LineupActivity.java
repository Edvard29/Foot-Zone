package com.example.footzone;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class LineupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lineup);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    public boolean onSupportNavigateUp() {
        finish(); // Закрыть активити при нажатии на стрелку "Назад"
        return true;
    }
}