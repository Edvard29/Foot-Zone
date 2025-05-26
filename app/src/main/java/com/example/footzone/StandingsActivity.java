package com.example.footzone;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footzone.adapter.StandingsAdapter;
import com.example.footzone.model.TeamStanding;
import com.example.footzone.network.ApiClient;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
public class StandingsActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private StandingsAdapter adapter;
    private Spinner leagueSpinner;
    private int selectedLeagueId;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_standings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize views
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        leagueSpinner = findViewById(R.id.spinner_league);

        // Set up NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        } else {
            Log.e("StandingsActivity", "NavigationView not found in layout");
        }

        // Set up adapter for Spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.league_names, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leagueSpinner.setAdapter(spinnerAdapter);

        // Listener for league selection
        leagueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedLeagueId = getLeagueId(position);
                loadStandings(selectedLeagueId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No action needed
            }
        });

        // Load data for the first league on start
        selectedLeagueId = getLeagueId(0);
        loadStandings(selectedLeagueId);
    }

    private int getLeagueId(int position) {
        int[] leagueIds = {39, 61, 78, 140, 135}; // League IDs
        return leagueIds[position];
    }

    private void loadStandings(int leagueId) {
        new Thread(() -> {
            ArrayList<TeamStanding> standings = new ArrayList<>();
            String jsonData = ApiClient.getFootballDataStandings(leagueId);
            Log.d("StandingsActivity", "JSON for league " + leagueId + ": " + jsonData.substring(0, Math.min(jsonData.length(), 1000)));

            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray response = jsonObject.getJSONArray("response");
                if (response.length() == 0) {
                    Log.w("StandingsActivity", "Empty response for league " + leagueId);
                    return;
                }
                JSONObject league = response.getJSONObject(0);
                JSONObject leagueInfo = league.getJSONObject("league");
                JSONArray leagueStandings = leagueInfo.getJSONArray("standings").getJSONArray(0);
                standings.add(new TeamStanding("League: " + leagueInfo.getString("name"), true));

                for (int i = 0; i < leagueStandings.length(); i++) {
                    JSONObject team = leagueStandings.getJSONObject(i);
                    JSONObject teamInfo = team.getJSONObject("team");
                    String teamName = teamInfo.getString("name");
                    String logoUrl = teamInfo.getString("logo");
                    int rank = team.getInt("rank");
                    int points = team.getInt("points");
                    JSONObject stats = team.getJSONObject("all");
                    int played = stats.getInt("played");
                    int wins = stats.getInt("win");
                    int losses = stats.getInt("lose");

                    standings.add(new TeamStanding(
                            rank + ". " + teamName,
                            points,
                            logoUrl,
                            played,
                            wins,
                            losses
                    ));
                    Log.d("StandingsActivity", "Team: " + teamName + ", Logo: " + logoUrl + ", Played: " + played + ", Wins: " + wins + ", Losses: " + losses);
                }
            } catch (Exception e) {
                Log.e("StandingsActivity", "Parsing error: " + e.getMessage(), e);
            }

            runOnUiThread(() -> {
                adapter = new StandingsAdapter(standings);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
                        recyclerView.getContext(), R.anim.layout_animation_fall_down));
                recyclerView.scheduleLayoutAnimation();
            });
        }).start();
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