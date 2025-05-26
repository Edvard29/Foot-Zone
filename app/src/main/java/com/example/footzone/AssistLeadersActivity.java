/*
 * AssistLeadersActivity.java
 * Displays a list of top assist providers for selected football leagues with player images.
 * Features:
 * - Spinner to select league.
 * - RecyclerView with MaterialCardView items showing player name, assists, and image.
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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.footzone.adapter.FootballerAdapter;
import com.example.footzone.model.Footballer;
import com.example.footzone.network.ApiClient;
import com.example.footzone.network.ApiResponseCallback;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;

public class AssistLeadersActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private FootballerAdapter adapter;
    private ProgressBar progressBar;
    private Spinner leagueSpinner;
    private List<Footballer> footballersList = new ArrayList<>();
    private static final int[] LEAGUE_IDS = {39, 140, 135, 78, 61}; // League IDs (e.g., Premier League, La Liga)

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_assist_leaders;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize views
        recyclerView = findViewById(R.id.recycler_view_assistants);
        progressBar = findViewById(R.id.progress_bar);
        leagueSpinner = findViewById(R.id.league_spinner);

        // Set up NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        } else {
            Log.e("AssistLeadersActivity", "NavigationView not found in layout");
        }

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
                fetchAssistants(LEAGUE_IDS[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        // Initial load for first league
        fetchAssistants(LEAGUE_IDS[0]);
    }

    private void fetchAssistants(int leagueId) {
        progressBar.setVisibility(View.VISIBLE);
        Log.d("AssistLeadersActivity", "🔄 Fetching assist leaders for league ID: " + leagueId);

        ApiClient.getAssistants(leagueId, 2024, new ApiResponseCallback() {
            @Override
            public void onSuccess(String jsonData) {
                Log.d("AssistLeadersActivity", "✅ API response: " + jsonData);
                List<Footballer> footballers = ApiClient.parseAssistants(jsonData);
                Log.d("AssistLeadersActivity", "📊 Found " + footballers.size() + " assist leaders");

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (footballers.isEmpty()) {
                        Log.w("AssistLeadersActivity", "⚠ No assist leaders data!");
                        Toast.makeText(AssistLeadersActivity.this, "No assist leaders data", Toast.LENGTH_SHORT).show();
                    } else {
                        footballersList.clear();
                        footballersList.addAll(footballers);
                        adapter = new FootballerAdapter(footballersList, false);
                        recyclerView.setAdapter(adapter);
                        Log.d("AssistLeadersActivity", "🔄 Adapter updated!");
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AssistLeadersActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                    Log.e("AssistLeadersActivity", "❌ Error fetching assist leaders: " + errorMessage);
                });
            }
        });
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