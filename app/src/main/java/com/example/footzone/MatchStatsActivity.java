package com.example.footzone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MatchStatsActivity extends AppCompatActivity {

    private static final String TAG = "MatchStatsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_stats);

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

        // Логирование данных
        Log.d(TAG, "Данные Intent: homeTeam=" + homeTeam + ", awayTeam=" + awayTeam + ", date=" + date +
                ", possession=" + possessionData + ", shotsOnGoal=" + shotsOnGoalData);

        // Проверка и установка данных
        if (homeTeam != null && awayTeam != null) {
            matchTeams.setText(homeTeam + " vs " + awayTeam);
        } else {
            matchTeams.setText("Неизвестные команды");
            Log.w(TAG, "Отсутствуют данные о командах");
        }

        matchDate.setText(date != null ? date : "Неизвестная дата");
        possession.setText(possessionData != null ? "Владение мячом: " + possessionData : "Владение мячом: Н/Д");
        shotsOnGoal.setText(shotsOnGoalData != null ? "Удары в створ: " + shotsOnGoalData : "Удары в створ: Н/Д");
        shotsOffGoal.setText(shotsOffGoalData != null ? "Удары мимо: " + shotsOffGoalData : "Удары мимо: Н/Д");
        corners.setText(cornersData != null ? "Угловые: " + cornersData : "Угловые: Н/Д");
        fouls.setText(foulsData != null ? "Фолы: " + foulsData : "Фолы: Н/Д");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}