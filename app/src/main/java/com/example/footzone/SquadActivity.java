package com.example.footzone;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footzone.adapter.MatchAdapter;
import com.example.footzone.adapter.SquadAdapter;
import com.example.footzone.model.Match;

import com.example.footzone.model.Player;
import com.example.footzone.network.ApiClient;
import com.example.footzone.network.FootballApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.content.Intent;

import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SquadActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SquadAdapter squadAdapter;
    private List<Player> playerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squad);

        recyclerView = findViewById(R.id.recycler_view_squad);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        int teamId = getIntent().getIntExtra("TEAM_ID", 33); // Передаваемый ID команды

        new Thread(() -> {
            String jsonData = ApiClient.getTeamSquad(teamId);
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
