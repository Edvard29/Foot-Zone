package com.example.footzone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.footzone.adapter.SquadAdapter;
import com.example.footzone.model.Player;
import com.example.footzone.network.ApiClient;
import com.google.android.material.navigation.NavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SquadActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private SquadAdapter squadAdapter;
    private List<Player> playerList = new ArrayList<>();
    private Spinner leagueSpinner;
    private Spinner teamSpinner;
    private Map<String, Integer> leagueMap = new HashMap<>(); // League Name -> League ID
    private Map<String, Integer> teamMap = new HashMap<>(); // Team Name -> Team ID
    private List<String> teamNames = new ArrayList<>(); // List to store team names for team spinner
    private static final int[] TOP_5_LEAGUE_IDS = {39, 61, 78, 140, 135}; // Premier League, Ligue 1, Bundesliga, La Liga, Serie A
    private static final String[] LEAGUE_NAMES = {"Premier League", "Ligue 1", "Bundesliga", "La Liga", "Serie A"};

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_squad;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize views
        leagueSpinner = findViewById(R.id.league_spinner);
        teamSpinner = findViewById(R.id.team_spinner);
        recyclerView = findViewById(R.id.recycler_view_squad);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        } else {
            Log.e("SquadActivity", "NavigationView not found in layout");
        }

        // Set up league spinner
        ArrayAdapter<String> leagueAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, LEAGUE_NAMES);
        leagueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leagueSpinner.setAdapter(leagueAdapter);

        // Initialize league map
        for (int i = 0; i < LEAGUE_NAMES.length; i++) {
            leagueMap.put(LEAGUE_NAMES[i], TOP_5_LEAGUE_IDS[i]);
        }

        // Listener for league selection
        leagueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String leagueName = parent.getItemAtPosition(position).toString();
                int leagueId = leagueMap.get(leagueName);
                loadTeamsForLeague(leagueId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Listener for team selection
        teamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String teamName = parent.getItemAtPosition(position).toString();
                Integer teamId = teamMap.get(teamName);
                if (teamId != null) {
                    loadSquad(teamId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Load teams for the first league
        loadTeamsForLeague(TOP_5_LEAGUE_IDS[0]);
    }

    private void loadTeamsForLeague(int leagueId) {
        new Thread(() -> {
            teamMap.clear();
            teamNames.clear();
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
                Log.e("SquadActivity", "Error loading teams: " + e.getMessage(), e);
            }

            runOnUiThread(() -> {
                if (teamNames.isEmpty()) {
                    teamNames.add("No teams available");
                    teamMap.put("No teams available", -1);
                }
                ArrayAdapter<String> teamAdapter = new ArrayAdapter<>(SquadActivity.this, android.R.layout.simple_spinner_item, teamNames);
                teamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                teamSpinner.setAdapter(teamAdapter);
                // Load squad for the first team if available
                if (!teamNames.isEmpty() && teamMap.get(teamNames.get(0)) != -1) {
                    loadSquad(teamMap.get(teamNames.get(0)));
                } else {
                    playerList.clear();
                    squadAdapter = new SquadAdapter(playerList);
                    recyclerView.setAdapter(squadAdapter);
                }
            });
        }).start();
    }

    private void loadSquad(int teamId) {
        if (teamId == -1) {
            runOnUiThread(() -> {
                playerList.clear();
                squadAdapter = new SquadAdapter(playerList);
                recyclerView.setAdapter(squadAdapter);
            });
            return;
        }

        new Thread(() -> {
            playerList.clear();
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
                            String photoUrl = playerObj.optString("photo", null);
                            playerList.add(new Player(name, position, age, photoUrl));
                            Log.d("SquadActivity", "Player: " + name + ", Photo: " + photoUrl);
                        }
                    } else {
                        Log.w("SquadActivity", "Empty player list for team ID: " + teamId);
                    }
                } catch (JSONException e) {
                    Log.e("SquadActivity", "Error parsing squad: " + e.getMessage(), e);
                }
            } else {
                Log.w("SquadActivity", "No data for team ID: " + teamId);
            }

            runOnUiThread(() -> {
                squadAdapter = new SquadAdapter(playerList);
                recyclerView.setAdapter(squadAdapter);
            });
        }).start();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.nav_user) {
            startActivity(new Intent(this, UserActivity.class));
        } else if (id == R.id.nav_squad) {
            // Already in SquadActivity, no action needed
        } else if (id == R.id.nav_standigs) {
            startActivity(new Intent(this, StandingsActivity.class));
        } else if (id == R.id.nav_select_team) {
            startActivity(new Intent(this, SelectTeamActivity.class));
        } else if (id == R.id.nav_scorers) {
            startActivity(new Intent(this, TopScorersActivity.class));
        } else if (id == R.id.nav_assists) {
            startActivity(new Intent(this, AssistLeadersActivity.class));
        } else if (id == R.id.nav_leader) {
            startActivity(new Intent(this, LeaderboardActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
        return true;
    }
}