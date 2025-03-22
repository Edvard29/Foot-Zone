package com.example.footzone;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.footzone.network.ApiClient;
import com.example.footzone.adapter.StandingsAdapter;
import com.example.footzone.network.FootballApi;
import java.util.ArrayList;
import com.example.footzone.model.TeamStanding;

import org.json.JSONArray;
import org.json.JSONObject;


public class StandingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StandingsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standings);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new Thread(() -> {
            ArrayList<TeamStanding> standings = new ArrayList<>();
            int[] leagues = {39, 61, 78, 140, 135};
            for (int leagueId : leagues) {
                String jsonData = ApiClient.getFootballDataStandings(leagueId);
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONArray response = jsonObject.getJSONArray("response");
                    JSONObject league = response.getJSONObject(0);
                    JSONObject leagueInfo = league.getJSONObject("league");
                    JSONArray leagueStandings = leagueInfo.getJSONArray("standings").getJSONArray(0);
                    standings.add(new TeamStanding("League: " + leagueInfo.getString("name"), true));
                    for (int i = 0; i < leagueStandings.length(); i++) {
                        JSONObject team = leagueStandings.getJSONObject(i);
                        String teamName = team.getJSONObject("team").getString("name");
                        int rank = team.getInt("rank");
                        int points = team.getInt("points");
                        standings.add(new TeamStanding(rank + ". " + teamName, false, points));


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(() -> {
                adapter = new StandingsAdapter(standings);
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }
}
