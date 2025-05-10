package com.example.footzone;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.footzone.adapter.TransferAdapter;
import com.example.footzone.model.Transfer;
import com.example.footzone.network.ApiClient;
import com.example.footzone.network.ApiResponseCallback;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class TransfersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TransferAdapter adapter;
    private ProgressBar progressBar;
    private List<Transfer> transferList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfers);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Transfers");
        }

        recyclerView = findViewById(R.id.recycler_view_transfers);
        progressBar = findViewById(R.id.progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransferAdapter(transferList);
        recyclerView.setAdapter(adapter);

        loadTransfers();
    }

    private void loadTransfers() {
        progressBar.setVisibility(View.VISIBLE);
        Log.d("TransferActivity", "🔄 Loading transfers...");

        ApiClient.getTransfers(2023, new ApiResponseCallback() {
            @Override
            public void onSuccess(String jsonData) {
                Log.d("TransferActivity", "✅ API response: " + jsonData.substring(0, Math.min(jsonData.length(), 100)));
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONArray responseArray = jsonObject.getJSONArray("response");

                    transferList.clear();
                    for (int i = 0; i < responseArray.length(); i++) {
                        JSONObject transferObj = responseArray.getJSONObject(i);
                        JSONObject player = transferObj.getJSONObject("player");
                        JSONArray transfers = transferObj.getJSONArray("transfers");

                        if (transfers.length() > 0) {
                            JSONObject latestTransfer = transfers.getJSONObject(0);
                            String playerName = player.getString("name");
                            String fromTeam = latestTransfer.getJSONObject("teams").getJSONObject("out").getString("name");
                            String toTeam = latestTransfer.getJSONObject("teams").getJSONObject("in").getString("name");
                            String transferDate = latestTransfer.getString("date");
                            String transferFee = latestTransfer.optString("type", "N/A");

                            Transfer transfer = new Transfer(playerName, fromTeam, toTeam, transferDate, transferFee);
                            transferList.add(transfer);
                            Log.d("TransferActivity", "Added transfer: " + playerName + " from " + fromTeam + " to " + toTeam);
                        }
                    }

                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                        if (transferList.isEmpty()) {
                            Toast.makeText(TransfersActivity.this, "No transfers available", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    Log.e("TransferActivity", "Error parsing transfers: " + e.getMessage(), e);
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(TransfersActivity.this, "Failed to load transfers", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("TransferActivity", "❌ Error fetching transfers: " + errorMessage);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(TransfersActivity.this, "Error fetching transfers: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}