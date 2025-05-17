package com.example.footzone;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.footzone.adapter.LineupPlayerAdapter;
import com.example.footzone.model.Player;
import java.util.ArrayList;
import java.util.List;

public class LineupActivity extends BaseActivity {

    private static final String TAG = "LineupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Team Lineups");
        }

        // Initialize views
        TextView homeTeamName = findViewById(R.id.home_team_name);
        TextView homeTeamFormation = findViewById(R.id.home_team_formation);
        ImageView homeTeamLogo = findViewById(R.id.home_team_logo);
        RecyclerView homeTeamRecyclerView = findViewById(R.id.home_team_recycler_view);

        TextView awayTeamName = findViewById(R.id.away_team_name);
        TextView awayTeamFormation = findViewById(R.id.away_team_formation);
        ImageView awayTeamLogo = findViewById(R.id.away_team_logo);
        RecyclerView awayTeamRecyclerView = findViewById(R.id.away_team_recycler_view);

        // Get data from Intent
        String homeTeam = getIntent().getStringExtra("homeTeam");
        String homeLineup = getIntent().getStringExtra("homeLineup");
        String awayTeam = getIntent().getStringExtra("awayTeam");
        String awayLineup = getIntent().getStringExtra("awayLineup");

        // Log input data
        Log.d(TAG, "homeTeam: " + homeTeam + ", homeLineup: " + homeLineup);
        Log.d(TAG, "awayTeam: " + awayTeam + ", awayLineup: " + awayLineup);

        // Set up home team
        homeTeamName.setText(homeTeam != null ? homeTeam : "Home Team");
        homeTeamFormation.setText(homeLineup != null && homeLineup.contains("(") ? homeLineup.substring(homeLineup.indexOf("(")) : "(4-4-2)");
        Glide.with(this)
                .load(R.drawable.ic_default_team_logo)
                .placeholder(R.drawable.ic_default_team_logo)
                .error(R.drawable.ic_default_team_logo)
                .into(homeTeamLogo);

        List<Player> homePlayers = parseLineupString(homeLineup);
        Log.d(TAG, "Home players parsed: " + homePlayers.size() + ", Players: " + homePlayers);
        homeTeamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        LineupPlayerAdapter homeAdapter = new LineupPlayerAdapter(homePlayers);
        homeTeamRecyclerView.setAdapter(homeAdapter);

        // Set up away team
        awayTeamName.setText(awayTeam != null ? awayTeam : "Away Team");
        awayTeamFormation.setText(awayLineup != null && awayLineup.contains("(") ? awayLineup.substring(awayLineup.indexOf("(")) : "(4-3-3)");
        Glide.with(this)
                .load(R.drawable.ic_default_team_logo)
                .placeholder(R.drawable.ic_default_team_logo)
                .error(R.drawable.ic_default_team_logo)
                .into(awayTeamLogo);

        List<Player> awayPlayers = parseLineupString(awayLineup);
        Log.d(TAG, "Away players parsed: " + awayPlayers.size() + ", Players: " + awayPlayers);
        awayTeamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        LineupPlayerAdapter awayAdapter = new LineupPlayerAdapter(awayPlayers);
        awayTeamRecyclerView.setAdapter(awayAdapter);
    }

    private List<Player> parseLineupString(String lineup) {
        List<Player> players = new ArrayList<>();
        if (lineup == null || lineup.trim().isEmpty()) {
            Log.w(TAG, "Lineup string is null or empty");
            players.add(new Player("Unknown Player", "N/A", 0, null));
            return players;
        }

        try {
            // Remove formation (e.g., "(4-4-2)") if present
            String playerDataString = lineup.contains("(") ? lineup.substring(0, lineup.indexOf("(")).trim() : lineup.trim();
            if (playerDataString.isEmpty()) {
                Log.w(TAG, "No player data after removing formation");
                players.add(new Player("Unknown Player", "N/A", 0, null));
                return players;
            }

            // Split by comma or newline
            String[] playerData = playerDataString.split(",|\\\n");
            for (String data : playerData) {
                data = data.trim();
                if (data.isEmpty()) {
                    Log.d(TAG, "Skipping empty player data");
                    continue;
                }

                // Find first colon to separate name (allowing spaces) from other fields
                int firstColon = data.indexOf(':');
                String name = firstColon >= 0 ? data.substring(0, firstColon).trim() : data.trim();
                if (name.isEmpty()) name = "Unknown";

                // Extract remaining fields after first colon
                String[] parts = firstColon >= 0 ? data.substring(firstColon + 1).split(":", -1) : new String[0];
                String position = parts.length > 0 && !parts[0].trim().isEmpty() ? parts[0].trim() : "N/A";
                int age = 0;
                if (parts.length > 1 && parts[1].trim().matches("\\d+")) {
                    age = Integer.parseInt(parts[1].trim());
                }
                String photoUrl = parts.length > 2 && !parts[2].trim().isEmpty() ? parts[2].trim() : null;

                Log.d(TAG, "Parsed player - Name: " + name + ", Position: " + position + ", Age: " + age + ", PhotoUrl: " + photoUrl);
                players.add(new Player(name, position, age, photoUrl));
            }

            if (players.isEmpty()) {
                Log.w(TAG, "No valid players parsed from: " + playerDataString);
                players.add(new Player("Unknown Player", "N/A", 0, null));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing lineup: " + lineup, e);
            players.add(new Player("Unknown Player", "N/A", 0, null));
        }

        return players;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_lineup;
    }
}