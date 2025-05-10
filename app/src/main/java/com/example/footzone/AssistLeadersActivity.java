package com.example.footzone;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footzone.adapter.FootballerAdapter;
import com.example.footzone.model.Footballer;
import com.example.footzone.network.ApiClient;
import com.example.footzone.network.ApiResponseCallback;

import java.util.ArrayList;
import java.util.List;

public class AssistLeadersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FootballerAdapter adapter;
    private ProgressBar progressBar;
    private Spinner leagueSpinner;
    private List<Footballer> footballersList = new ArrayList<>();

    private static final int[] LEAGUE_IDS = {39, 140, 135, 78, 61}; // IDs лиг (например: Премьер-Лига, Ла Лига, Серия A, и т.д.)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assist_leaders);

        recyclerView = findViewById(R.id.recycler_view_assistants);
        progressBar = findViewById(R.id.progress_bar);
        leagueSpinner = findViewById(R.id.league_spinner);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Настройка Spinner для выбора лиги
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.leagues, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leagueSpinner.setAdapter(adapter);

        // Слушатель для выбора лиги
        leagueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                fetchAssistants(LEAGUE_IDS[position]); // Запросить ассистентов для выбранной лиги
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // Изначально загрузим данные для первой лиги (например, Премьер-Лига)
        fetchAssistants(LEAGUE_IDS[0]);
    }

    private void fetchAssistants(int leagueId) {
        progressBar.setVisibility(View.VISIBLE);
        Log.d("AssistLeadersActivity", "🔄 Запрос ассистентов для лиги с ID: " + leagueId);

        ApiClient.getAssistants(leagueId, 2024, new ApiResponseCallback() {
            @Override
            public void onSuccess(String jsonData) {
                Log.d("AssistLeadersActivity", "✅ API ответ: " + jsonData);

                List<Footballer> footballers = ApiClient.parseAssistants(jsonData);
                Log.d("AssistLeadersActivity", "📊 Найдено ассистентов: " + footballers.size());

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (footballers.isEmpty()) {
                        Log.w("AssistLeadersActivity", "⚠ Нет данных о ассистентах!");
                        Toast.makeText(AssistLeadersActivity.this, "Нет данных о ассистентах", Toast.LENGTH_SHORT).show();
                    } else {
                        footballersList.clear();
                        footballersList.addAll(footballers);
                        adapter = new FootballerAdapter(footballersList, false); // false, так как показываем ассистентов
                        recyclerView.setAdapter(adapter);
                        Log.d("AssistLeadersActivity", "🔄 Данные обновлены в адаптере!");
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AssistLeadersActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    Log.e("AssistLeadersActivity", "❌ Ошибка загрузки ассистентов: " + errorMessage);
                });
            }
        });
    }
}