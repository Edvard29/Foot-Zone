package com.example.footzone;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footzone.adapter.TransferAdapter;
import com.example.footzone.network.ApiClient;
import com.example.footzone.network.ApiResponseCallback;
import com.example.footzone.model.Transfer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TransfersActivity extends AppCompatActivity {

    private RecyclerView transfersRecyclerView;
    private TransferAdapter transferAdapter;
    private List<Transfer> transferList = new ArrayList<>();

    // Пример id лиги и сезона (можно заменить на актуальные данные)
    private int leagueId = 39; // Например, английская Премьер-лига
    private int seasonYear = 2025;

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
        ApiClient.getTransfers(leagueId, seasonYear, new ApiResponseCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    // Преобразуем ответ в JSON
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray transfersArray = jsonResponse.getJSONArray("response");

                    // Очищаем список перед добавлением новых данных
                    transferList.clear();

                    // Проходим по массиву трансферов и добавляем их в список
                    for (int i = 0; i < transfersArray.length(); i++) {
                        JSONObject transferObj = transfersArray.getJSONObject(i);

                        String playerName = transferObj.getJSONObject("player").getString("name");
                        String fromTeam = transferObj.getJSONObject("teams").getString("from");
                        String toTeam = transferObj.getJSONObject("teams").getString("to");
                        String transferDate = transferObj.getString("date");

                        Transfer transfer = new Transfer(playerName, fromTeam, toTeam, transferDate);
                        transferList.add(transfer);
                    }

                    // Уведомляем адаптер о том, что данные обновились
                    transferAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e("TransfersActivity", "Ошибка при парсинге данных: " + e.getMessage());
                    Toast.makeText(TransfersActivity.this, "Ошибка при получении данных", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String error) {
                // Обработка ошибок
                Log.e("TransfersActivity", "Ошибка: " + error);
                Toast.makeText(TransfersActivity.this, "Ошибка при загрузке трансферов", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
