package com.example.footzone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;

import com.example.footzone.adapter.TransferAdapter;
import com.example.footzone.model.TransferItem;
import com.example.footzone.network.ApiClient;
import com.example.footzone.network.FootballApi;

import java.util.ArrayList;


public class TransfersActivity extends AppCompatActivity {
    private androidx.recyclerview.widget.RecyclerView recyclerView;
    ;
    private TransferAdapter adapter;
    private ArrayList<TransferItem> transfers = new ArrayList<>(); // Объявляем один раз

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfers);

        recyclerView = findViewById(R.id.recycler_view_transfers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new Thread(() -> {
            String jsonTransfers = ApiClient.getFootballTransfers(39, 2021);
            Log.d("TransfersActivity", "Ответ API: " + jsonTransfers); // Проверка API-ответа

            if (jsonTransfers != null) {
                transfers = FootballApi.parseTransfers(jsonTransfers); // Используем уже объявленный список
                Log.d("TransfersActivity", "Количество трансферов: " + transfers.size());

                runOnUiThread(() -> {
                    adapter = new TransferAdapter(transfers);
                    recyclerView.setAdapter(adapter);
                });
            }
            runOnUiThread(() -> {
                adapter = new TransferAdapter(transfers);
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }
}
