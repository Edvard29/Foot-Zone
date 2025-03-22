package com.example.footzone;

import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footzone.adapter.MatchAdapter;
import com.example.footzone.model.Match;
import com.example.footzone.network.ApiClient;
import com.example.footzone.network.FootballApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private MatchAdapter matchAdapter;
    private List<Integer> topLeagues;
    private ExecutorService executorService = Executors.newFixedThreadPool(3); // Оптимизированные потоки

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

        topLeagues = Arrays.asList(39, 61, 78, 140, 135); // Топ-5 лиг

        fetchMatches();
    }

    private void fetchMatches() {
        Log.d("MainActivity", "Fetching football data...");

        executorService.execute(() -> {
            ArrayList<Match> allMatches = new ArrayList<>();

            for (int leagueId : topLeagues) {
                String jsonData = ApiClient.getMatches(leagueId); // Теперь API должен использовать сезон 2024
                if (jsonData != null) {
                    Log.d("MainActivity", "Fetched data for league " + leagueId);
                    ArrayList<Match> matches = FootballApi.parseMatches(jsonData);

                    if (matches != null) {
                        allMatches.addAll(matches);
                    } else {
                        Log.e("MainActivity", "Error parsing matches for league " + leagueId);
                    }
                } else {
                    Log.e("MainActivity", "No data received for league " + leagueId);
                }
            }

            Log.d("MainActivity", "Total matches fetched: " + allMatches.size());

            if (!allMatches.isEmpty()) {
                runOnUiThread(() -> {
                    matchAdapter = new MatchAdapter(allMatches);
                    recyclerView.setAdapter(matchAdapter);
                });
            }
        });
    }
}
