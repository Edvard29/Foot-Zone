package com.example.footzone;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footzone.adapter.TransferAdapter;
import com.example.footzone.network.ApiClient;
import com.example.footzone.model.Transfer;
import com.example.footzone.network.ApiResponseCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransfersActivity extends AppCompatActivity {

    private RecyclerView transfersRecyclerView;
    private TransferAdapter transferAdapter;
    private List<Transfer> transferList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfers);

        transfersRecyclerView = findViewById(R.id.recycler_view_transfers);
        transfersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transferAdapter = new TransferAdapter(transferList);
        transfersRecyclerView.setAdapter(transferAdapter);

        // Загружаем трансферы
        loadTransfers();
    }

    private void loadTransfers() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Вместо leagueId и seasonYear теперь используем реальные ID команды и игрока
            int playerId = 35845; // Заменить на актуальный ID игрока
            int teamId = 1126; // Заменить на актуальный ID команды

            try {
                ApiClient.getTransfers(playerId, teamId, new ApiResponseCallback() {
                    @Override
                    public void onSuccess(String response) {
                        runOnUiThread(() -> {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                JSONArray transfersArray = jsonResponse.getJSONArray("response");

                                transferList.clear();

                                for (int i = 0; i < transfersArray.length(); i++) {
                                    JSONObject transferObj = transfersArray.getJSONObject(i);

                                    String playerName = transferObj.getJSONObject("player").getString("name");
                                    String fromTeam = transferObj.getJSONObject("teams").getJSONObject("out").getString("name");
                                    String toTeam = transferObj.getJSONObject("teams").getJSONObject("in").getString("name");
                                    String transferDate = transferObj.getString("date");

                                    Transfer transfer = new Transfer(playerName, fromTeam, toTeam, transferDate);
                                    transferList.add(transfer);
                                }

                                transferAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                Log.e("TransfersActivity", "Ошибка при парсинге данных: " + e.getMessage());
                                Toast.makeText(TransfersActivity.this, "Ошибка при получении данных", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUiThread(() -> {
                            Log.e("TransfersActivity", "Ошибка: " + error);
                            Toast.makeText(TransfersActivity.this, "Ошибка при загрузке трансферов", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


}
