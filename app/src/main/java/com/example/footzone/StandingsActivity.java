package com.example.footzone;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footzone.adapter.StandingsAdapter;
import com.example.footzone.model.TeamStanding;
import com.example.footzone.network.ApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class StandingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StandingsAdapter adapter;
    private Spinner leagueSpinner;
    private int selectedLeagueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standings);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        leagueSpinner = findViewById(R.id.spinner_league);

        // Настроим адаптер для Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.league_names, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leagueSpinner.setAdapter(adapter);

        // Обработчик выбора лиги
        leagueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Выбираем лигу по её позиции в списке
                selectedLeagueId = getLeagueId(position);
                loadStandings(selectedLeagueId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Действия, если ничего не выбрано
            }
        });

        // Загрузка данных для первой лиги при запуске
        selectedLeagueId = getLeagueId(0);
        loadStandings(selectedLeagueId);
    }

    private int getLeagueId(int position) {
        // Соответствие позиции в Spinner ID лиги
        int[] leagueIds = {39, 61, 78, 140, 135}; // Пример ID лиг
        return leagueIds[position];
    }

    private void loadStandings(int leagueId) {
        // Загружаем таблицу для выбранной лиги
        new Thread(() -> {
            ArrayList<TeamStanding> standings = new ArrayList<>();
            String jsonData = ApiClient.getFootballDataStandings(leagueId);

            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray response = jsonObject.getJSONArray("response");
                JSONObject league = response.getJSONObject(0);
                JSONObject leagueInfo = league.getJSONObject("league");
                JSONArray leagueStandings = leagueInfo.getJSONArray("standings").getJSONArray(0);
                standings.add(new TeamStanding("League: " + leagueInfo.getString("name"), true, 0));

                for (int i = 0; i < leagueStandings.length(); i++) {
                    JSONObject team = leagueStandings.getJSONObject(i);
                    String teamName = team.getJSONObject("team").getString("name");
                    int rank = team.getInt("rank");
                    int points = team.getInt("points");
                    standings.add(new TeamStanding(rank + ". " + teamName, points));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                adapter = new StandingsAdapter(standings);
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }
}
