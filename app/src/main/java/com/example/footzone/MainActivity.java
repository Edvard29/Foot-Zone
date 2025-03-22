package com.example.footzone;

import android.os.Bundle;
import android.util.Log;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.footzone.adapter.MatchAdapter;
import com.example.footzone.model.Match;
import com.example.footzone.network.ApiClient;
import com.example.footzone.network.ApiResponseCallback;
import com.example.footzone.network.FootballApi;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        topLeagues = Arrays.asList(39, 61, 78, 140, 135); // Топ-5 лиг

        fetchMatches();
    }

    private void fetchMatches() {
        Log.d("MainActivity", "Fetching football data...");

        for (int leagueId : topLeagues) {
            ApiClient.getMatches(leagueId, new ApiResponseCallback() {
                @Override
                public void onSuccess(String jsonData) {
                    Log.d("MainActivity", "Fetched data for league " + leagueId);

                    List<Match> matches = FootballApi.parseMatches(jsonData);

                    if (matches == null) {
                        Log.e("MainActivity", "Error parsing matches for league " + leagueId);
                        return;
                    }

                    List<Match> filteredMatches = new ArrayList<>();
                    for (Match match : matches) {
                        if ("NS".equals(match.getStatus()) || "FT".equals(match.getStatus())) {
                            filteredMatches.add(match);
                        }
                    }

                    runOnUiThread(() -> {
                        allMatches.addAll(filteredMatches);
                        updateRecyclerView(allMatches);
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("MainActivity", "Error fetching data for league " + leagueId + ": " + errorMessage);
                }
            });
        }
    }

    private void updateRecyclerView(List<Match> matches) {
        if (matchAdapter == null) {
            matchAdapter = new MatchAdapter(matches);
            recyclerView.setAdapter(matchAdapter);
        } else {
            matchAdapter.notifyDataSetChanged();
        }
    }
}
