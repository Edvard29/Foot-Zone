package com.example.footzone;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footzone.adapter.SquadAdapter;
import com.example.footzone.model.Player;
import com.example.footzone.network.ApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SquadActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SquadAdapter squadAdapter;
    private List<Player> playerList = new ArrayList<>();
    private Spinner teamSpinner;

    private Map<String, Integer> teamMap = new HashMap<>(); // Название -> ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squad);

        teamSpinner = findViewById(R.id.team_spinner);
        recyclerView = findViewById(R.id.recycler_view_squad);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadTeamsFromTop5Leagues(); // Загружаем команды
    }

    private void loadTeamsFromTop5Leagues() {
        new Thread(() -> {
            int[] top5LeagueIds = {39, 78, 61, 140, 135}; // Англия, Германия, Франция, Испания, Италия
            List<String> teamNames = new ArrayList<>();

            for (int leagueId : top5LeagueIds) {
                String jsonData = ApiClient.getTeamsByLeague(leagueId, 2024);
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONArray response = jsonObject.getJSONArray("response");
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject team = response.getJSONObject(i).getJSONObject("team");
                        String name = team.getString("name");
                        int id = team.getInt("id");
                        teamNames.add(name);
                        teamMap.put(name, id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teamNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                teamSpinner.setAdapter(adapter);

                teamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String teamName = parent.getItemAtPosition(position).toString();
                        int teamId = teamMap.get(teamName);
                        loadSquad(teamId);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                });
            });
        }).start();
    }

    private void loadSquad(int teamId) {
        new Thread(() -> {
            playerList.clear();
            String jsonData = ApiClient.getTeamSquad(teamId); // <-- Заменено на правильный метод
            if (jsonData != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONArray response = jsonObject.getJSONArray("response");
                    if (response.length() > 0) {
                        JSONArray players = response.getJSONObject(0).getJSONArray("players");
                        for (int i = 0; i < players.length(); i++) {
                            JSONObject playerObj = players.getJSONObject(i);
                            String name = playerObj.getString("name");
                            String position = playerObj.getString("position");
                            int age = playerObj.getInt("age");
                            playerList.add(new Player(name, position, age));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(() -> {
                squadAdapter = new SquadAdapter(playerList);
                recyclerView.setAdapter(squadAdapter);
            });
        }).start();
    }
}
