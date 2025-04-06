package com.example.footzone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.footzone.adapter.MatchAdapter;
import com.example.footzone.model.Match;
import com.example.footzone.network.ApiClient;
import com.example.footzone.network.ApiResponseCallback;
import com.example.footzone.network.FootballApi;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private MatchAdapter matchAdapter;
    private List<Integer> topLeagues;
    private final List<Match> allMatches = new ArrayList<>();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(true);

        topLeagues = Arrays.asList(39, 61, 78, 140, 135);

        fetchMatches();
    }

    private void fetchMatches() {
        Log.d("MainActivity", "Fetching football data...");
        AtomicInteger completedRequests = new AtomicInteger(0);

        for (int leagueId : topLeagues) {
            ApiClient.getMatches(leagueId, new ApiResponseCallback() {
                @Override
                public void onSuccess(String jsonData) {
                    Log.d("MainActivity", "Received data for league " + leagueId + ": " + jsonData.substring(0, Math.min(jsonData.length(), 100)));
                    List<Match> matches = FootballApi.parseMatches(jsonData);
                    if (matches == null || matches.isEmpty()) {
                        Log.e("MainActivity", "No matches parsed for league " + leagueId);
                        handleRequestCompletion(completedRequests);
                        return;
                    }

                    List<Match> filteredMatches = new ArrayList<>();
                    for (Match match : matches) {
                        if (isDateOnOrAfterTarget(match.getDate())) {
                            filteredMatches.add(match);
                            Log.d("MainActivity", "Match added: " + match.getHomeTeam() + " vs " + match.getAwayTeam() + ", Date: " + match.getDate());
                        }
                    }
                    Log.d("MainActivity", "Filtered " + filteredMatches.size() + " matches for league " + leagueId);

                    synchronized (allMatches) {
                        allMatches.addAll(filteredMatches);
                    }
                    handleRequestCompletion(completedRequests);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("MainActivity", "Error fetching data for league " + leagueId + ": " + errorMessage);
                    handleRequestCompletion(completedRequests);
                }
            });
        }
    }

    private boolean isDateOnOrAfterTarget(String matchDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date target = sdf.parse("2025-04-01"); // Фильтр с 1 апреля 2025 года
            Date match = sdf.parse(matchDate.substring(0, 10)); // Берем только дату (без времени)
            return match.compareTo(target) >= 0; // Возвращаем true, если дата матча >= 2025-04-01
        } catch (Exception e) {
            Log.e("MainActivity", "Date parse error for " + matchDate + ": " + e.getMessage());
            return false;
        }
    }

    private void handleRequestCompletion(AtomicInteger completedRequests) {
        if (completedRequests.incrementAndGet() == topLeagues.size()) {
            runOnUiThread(() -> {
                Log.d("MainActivity", "All matches loaded: " + allMatches.size());
                sortMatchesByDate();
                matchAdapter = new MatchAdapter(allMatches, new MatchAdapter.OnMatchActionListener() {
                    @Override
                    public void onShowLineups(Match match) {
                        fetchLineupsForMatch(match);
                    }

                    @Override
                    public void onShowStats(Match match) {
                        fetchStatsForMatch(match);
                    }

                    @Override
                    public void onShowChat(Match match) {
                        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        String username = prefs.getString("username", null);
                        Intent intent;
                        if (username == null) {
                            intent = new Intent(MainActivity.this, UsernameActivity.class);
                        } else {
                            intent = new Intent(MainActivity.this, ChatActivity.class);
                        }
                        intent.putExtra("fixtureId", match.getFixtureId());
                        intent.putExtra("matchTitle", match.getHomeTeam() + " vs " + match.getAwayTeam());
                        startActivity(intent);
                    }

                    @Override
                    public void onShowPrediction(Match match) {
                        Intent intent = new Intent(MainActivity.this, PredictionActivity.class);
                        intent.putExtra("fixtureId", match.getFixtureId());
                        intent.putExtra("matchTitle", match.getHomeTeam() + " vs " + match.getAwayTeam());
                        intent.putExtra("matchDate", match.getDate());
                        intent.putExtra("matchStatus", match.getStatus());
                        startActivity(intent);
                    }
                });
                recyclerView.setAdapter(matchAdapter);
                if (allMatches.isEmpty()) {
                    Toast.makeText(MainActivity.this, "No matches available", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchLineupsForMatch(Match match) {
        ApiClient.getLineups(match.getFixtureId(), new ApiResponseCallback() {
            @Override
            public void onSuccess(String jsonData) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONArray responseArray = jsonObject.getJSONArray("response");
                    if (responseArray.length() >= 2) {
                        JSONObject homeTeam = responseArray.getJSONObject(0);
                        JSONObject awayTeam = responseArray.getJSONObject(1);

                        String homeLineup = parseLineup(homeTeam);
                        String awayLineup = parseLineup(awayTeam);

                        Intent intent = new Intent(MainActivity.this, LineupActivity.class);
                        intent.putExtra("homeTeam", match.getHomeTeam());
                        intent.putExtra("homeLineup", homeLineup);
                        intent.putExtra("awayTeam", match.getAwayTeam());
                        intent.putExtra("awayLineup", awayLineup);
                        startActivity(intent);
                    } else {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Lineups not available", Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    Log.e("MainActivity", "Error parsing lineups: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error loading lineups", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("MainActivity", "Error fetching lineups: " + errorMessage);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to load lineups", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void fetchStatsForMatch(Match match) {
        ApiClient.getMatchStatistics(match.getFixtureId(), new ApiResponseCallback() {
            @Override
            public void onSuccess(String jsonData) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONArray responseArray = jsonObject.getJSONArray("response");
                    if (responseArray.length() >= 2) {
                        JSONObject homeStats = responseArray.getJSONObject(0);
                        JSONObject awayStats = responseArray.getJSONObject(1);

                        String possession = getStat(homeStats, "Ball Possession") + " - " + getStat(awayStats, "Ball Possession");
                        String shotsOnGoal = getStat(homeStats, "Shots on Goal") + " - " + getStat(awayStats, "Shots on Goal");
                        String shotsOffGoal = getStat(homeStats, "Shots off Goal") + " - " + getStat(awayStats, "Shots off Goal");
                        String corners = getStat(homeStats, "Corner Kicks") + " - " + getStat(awayStats, "Corner Kicks");
                        String fouls = getStat(homeStats, "Fouls") + " - " + getStat(awayStats, "Fouls");

                        Intent intent = new Intent(MainActivity.this, MatchStatsActivity.class);
                        intent.putExtra("homeTeam", match.getHomeTeam());
                        intent.putExtra("awayTeam", match.getAwayTeam());
                        intent.putExtra("date", match.getDate().substring(0, 10));
                        intent.putExtra("possession", possession);
                        intent.putExtra("shotsOnGoal", shotsOnGoal);
                        intent.putExtra("shotsOffGoal", shotsOffGoal);
                        intent.putExtra("corners", corners);
                        intent.putExtra("fouls", fouls);
                        startActivity(intent);
                    } else {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Statistics not available", Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    Log.e("MainActivity", "Error parsing stats: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error loading stats", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("MainActivity", "Error fetching stats: " + errorMessage);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to load stats", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private String getStat(JSONObject stats, String statName) {
        JSONArray statistics = stats.optJSONArray("statistics");
        if (statistics != null) {
            for (int i = 0; i < statistics.length(); i++) {
                JSONObject stat = statistics.optJSONObject(i);
                if (stat != null && statName.equals(stat.optString("type"))) {
                    String value = stat.optString("value", "0");
                    return value.isEmpty() ? "0" : value;
                }
            }
        }
        return "0";
    }

    private String parseLineup(JSONObject teamData) throws Exception {
        StringBuilder lineup = new StringBuilder();
        JSONArray players = teamData.getJSONArray("startXI");
        for (int i = 0; i < players.length(); i++) {
            JSONObject player = players.getJSONObject(i).getJSONObject("player");
            lineup.append(player.getString("name")).append("\n");
        }
        return lineup.toString();
    }

    private void sortMatchesByDate() {
        Collections.sort(allMatches, new Comparator<Match>() {
            @Override
            public int compare(Match match1, Match match2) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                    Date date1 = sdf.parse(match1.getDate());
                    Date date2 = sdf.parse(match2.getDate());
                    return date1.compareTo(date2);
                } catch (Exception e) {
                    Log.e("MainActivity", "Sort error: " + e.getMessage());
                    return 0;
                }
            }
        });
    }
}