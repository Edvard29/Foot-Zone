/*
 * TopScorersActivity.java
 * Displays a list of top goal scorers for selected football leagues with player images.
 * Features:
 * - Spinner to select league.
 * - RecyclerView with MaterialCardView items showing player name, goals, and image.
 * - Light green gradient background and Material Design styling.
 * - ProgressBar for loading state.
 * - Extends BaseActivity for consistency.
 *
 * Dependencies:
 * - Glide: implementation 'com.github.bumptech.glide:glide:4.16.0'
 * - Material Components: implementation 'com.google.android.material:material:1.12.0'
 * - RecyclerView: implementation 'androidx.recyclerview:recyclerview:1.3.2'
 */
package com.example.footzone;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.footzone.adapter.FootballerAdapter;
import com.example.footzone.model.Footballer;
import com.example.footzone.network.ApiClient;
import com.example.footzone.network.ApiResponseCallback;
import java.util.ArrayList;
import java.util.List;

public class TopScorersActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private FootballerAdapter adapter;
    private ProgressBar progressBar;
    private Spinner leagueSpinner;
    private List<Footballer> footballersList = new ArrayList<>();
    private static final int[] LEAGUE_IDS = {39, 140, 135, 78, 61}; // League IDs (e.g., Premier League, La Liga)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_scorers);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Top Scorers");
        }

        recyclerView = findViewById(R.id.recycler_view_top_scorers);
        progressBar = findViewById(R.id.progress_bar);
        leagueSpinner = findViewById(R.id.league_spinner);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up Spinner for league selection
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.leagues, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leagueSpinner.setAdapter(spinnerAdapter);

        // Listener for league selection
        leagueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                fetchTopScorers(LEAGUE_IDS[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        // Initial load for first league
        fetchTopScorers(LEAGUE_IDS[0]);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_top_scorers;
    }

    private void fetchTopScorers(int leagueId) {
        progressBar.setVisibility(View.VISIBLE);
        Log.d("TopScorersActivity", "🔄 Fetching top scorers for league ID: " + leagueId);

        ApiClient.getTopScorers(leagueId, 2024, new ApiResponseCallback() {
            @Override
            public void onSuccess(String jsonData) {
                Log.d("TopScorersActivity", "✅ API response: " + jsonData);
                List<Footballer> footballers = ApiClient.parseTopScorers(jsonData);
                Log.d("TopScorersActivity", "📊 Found " + footballers.size() + " top scorers");

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (footballers.isEmpty()) {
                        Log.w("TopScorersActivity", "⚠ No top scorers data!");
                        Toast.makeText(TopScorersActivity.this, "No top scorers data", Toast.LENGTH_SHORT).show();
                    } else {
                        footballersList.clear();
                        footballersList.addAll(footballers);
                        adapter = new FootballerAdapter(footballersList, true);
                        recyclerView.setAdapter(adapter);
                        Log.d("TopScorersActivity", "🔄 Adapter updated!");
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(TopScorersActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                    Log.e("TopScorersActivity", "❌ Error fetching top scorers: " + errorMessage);
                });
            }
        });
    }
}