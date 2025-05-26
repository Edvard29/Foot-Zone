package com.example.footzone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.footzone.adapter.LineupPlayerAdapter;
import com.example.footzone.model.Player;
import com.example.footzone.network.ApiClient;
import com.example.footzone.network.ApiResponseCallback;
import com.google.android.material.navigation.NavigationView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class LineupActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "LineupActivity";
    private TextView homeTeamName, homeTeamFormation, awayTeamName, awayTeamFormation;
    private ImageView homeTeamLogo, awayTeamLogo;
    private RecyclerView homeTeamRecyclerView, awayTeamRecyclerView;
    private ProgressBar progressBar;
    private List<Player> homePlayers = new ArrayList<>();
    private List<Player> awayPlayers = new ArrayList<>();
    private int homeTeamId, awayTeamId, fixtureId;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_lineup;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize views
        homeTeamName = findViewById(R.id.home_team_name);

        homeTeamRecyclerView = findViewById(R.id.home_team_recycler_view);
        awayTeamName = findViewById(R.id.away_team_name);


        awayTeamRecyclerView = findViewById(R.id.away_team_recycler_view);
        progressBar = findViewById(R.id.progress_bar);

        // Set up NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        } else {
            Log.e(TAG, "NavigationView not found in layout");
        }

        // Get data from Intent
        Intent intent = getIntent();
        String homeTeam = intent.getStringExtra("homeTeam");
        String homeLineup = intent.getStringExtra("homeLineup");
        String awayTeam = intent.getStringExtra("awayTeam");
        String awayLineup = intent.getStringExtra("awayLineup");
        fixtureId = intent.getIntExtra("fixtureId", -1);
        homeTeamId = intent.getIntExtra("homeTeamId", -1);
        awayTeamId = intent.getIntExtra("awayTeamId", -1);

        // Log input data for debugging
        Log.d(TAG, "Received Intent extras - fixtureId: " + fixtureId + ", homeTeamId: " + homeTeamId +
                ", awayTeamId: " + awayTeamId + ", homeTeam: " + (homeTeam != null ? homeTeam : "null") +
                ", homeLineup: " + (homeLineup != null ? homeLineup : "null") +
                ", awayTeam: " + (awayTeam != null ? awayTeam : "null") +
                ", awayLineup: " + (awayLineup != null ? awayLineup : "null"));

        // Validate essential Intent extras




        // Set team names and formations
        homeTeamName.setText(homeTeam != null && !homeTeam.isEmpty() ? homeTeam : "Home Team");
        awayTeamName.setText(awayTeam != null && !awayTeam.isEmpty() ? awayTeam : "Away Team");

        // Set up RecyclerViews
        setupRecyclerViews();

        // Load data
        loadSquads();
    }

    private void setupRecyclerViews() {
        homeTeamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        awayTeamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        LineupPlayerAdapter homeAdapter = new LineupPlayerAdapter(homePlayers);
        LineupPlayerAdapter awayAdapter = new LineupPlayerAdapter(awayPlayers);
        homeTeamRecyclerView.setAdapter(homeAdapter);
        awayTeamRecyclerView.setAdapter(awayAdapter);
    }





    private void loadSquads() {
        progressBar.setVisibility(View.VISIBLE);

        // Load home team squad
        ApiClient.getSquad(homeTeamId, new ApiResponseCallback() {
            @Override
            public void onSuccess(String jsonData) {
                try {
                    Log.d(TAG, "Home squad JSON: " + jsonData);
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONArray playersArray = jsonObject.getJSONArray("response").getJSONObject(0).getJSONArray("players");
                    homePlayers.clear();
                    for (int i = 0; i < playersArray.length(); i++) {
                        JSONObject playerObj = playersArray.getJSONObject(i);
                        String name = playerObj.getString("name");
                        Log.d(TAG, "Parsed home player: " + name);
                        homePlayers.add(new Player(name, "", 0, ""));
                    }
                    runOnUiThread(() -> {
                        homeTeamRecyclerView.getAdapter().notifyDataSetChanged();
                        if (!awayPlayers.isEmpty()) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing home squad JSON: " + e.getMessage(), e);
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        homePlayers.addAll(parseLineupString(getIntent().getStringExtra("homeLineup")));
                        homeTeamRecyclerView.getAdapter().notifyDataSetChanged();
                    });
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Error fetching home squad: " + errorMessage);
                runOnUiThread(() -> {
                    Toast.makeText(LineupActivity.this, "Error fetching home squad", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    homePlayers.addAll(parseLineupString(getIntent().getStringExtra("homeLineup")));
                    homeTeamRecyclerView.getAdapter().notifyDataSetChanged();
                });
            }
        });

        // Load away team squad
        ApiClient.getSquad(awayTeamId, new ApiResponseCallback() {
            @Override
            public void onSuccess(String jsonData) {
                try {
                    Log.d(TAG, "Away squad JSON: " + jsonData);
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONArray playersArray = jsonObject.getJSONArray("response").getJSONObject(0).getJSONArray("players");
                    awayPlayers.clear();
                    for (int i = 0; i < playersArray.length(); i++) {
                        JSONObject playerObj = playersArray.getJSONObject(i);
                        String name = playerObj.getString("name");
                        Log.d(TAG, "Parsed away player: " + name);
                        awayPlayers.add(new Player(name, "", 0, ""));
                    }
                    runOnUiThread(() -> {
                        awayTeamRecyclerView.getAdapter().notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing away squad JSON: " + e.getMessage(), e);
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        awayPlayers.addAll(parseLineupString(getIntent().getStringExtra("awayLineup")));
                        awayTeamRecyclerView.getAdapter().notifyDataSetChanged();
                    });
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Error fetching away squad: " + errorMessage);
                runOnUiThread(() -> {
                    Toast.makeText(LineupActivity.this, "Error fetching away squad", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    awayPlayers.addAll(parseLineupString(getIntent().getStringExtra("awayLineup")));
                    awayTeamRecyclerView.getAdapter().notifyDataSetChanged();
                });
            }
        });
    }

    private List<Player> parseLineupString(String lineup) {
        List<Player> players = new ArrayList<>();
        if (lineup == null || lineup.trim().isEmpty()) {
            Log.w(TAG, "Lineup string is null or empty, adding default players");
            players.add(new Player("Player 1", "", 0, ""));
            players.add(new Player("Player 2", "", 0, ""));
            players.add(new Player("Player 3", "", 0, ""));
            return players;
        }

        try {
            String playerDataString = lineup.contains("(") ? lineup.substring(0, lineup.indexOf("(")).trim() : lineup.trim();
            if (playerDataString.isEmpty()) {
                Log.w(TAG, "No player data after removing formation, adding default players");
                players.add(new Player("Player 1", "", 0, ""));
                players.add(new Player("Player 2", "", 0, ""));
                players.add(new Player("Player 3", "", 0, ""));
                return players;
            }

            String[] playerData = playerDataString.split(",|\\\n");
            for (String data : playerData) {
                data = data.trim();
                if (data.isEmpty()) {
                    Log.d(TAG, "Skipping empty player data");
                    continue;
                }

                int firstColon = data.indexOf(':');
                String name = firstColon >= 0 ? data.substring(0, firstColon).trim() : data.trim();
                if (name.isEmpty()) name = "Unknown";

                Log.d(TAG, "Parsed player - Name: " + name);
                players.add(new Player(name, "", 0, ""));
            }

            if (players.isEmpty()) {
                Log.w(TAG, "No valid players parsed from: " + playerDataString + ", adding default players");
                players.add(new Player("Player 1", "", 0, ""));
                players.add(new Player("Player 2", "", 0, ""));
                players.add(new Player("Player 3", "", 0, ""));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing lineup: " + lineup + ", adding default players", e);
            players.add(new Player("Player 1", "", 0, ""));
            players.add(new Player("Player 2", "", 0, ""));
            players.add(new Player("Player 3", "", 0, ""));
        }

        return players;
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