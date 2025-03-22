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

public class TopScorersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FootballerAdapter adapter;
    private ProgressBar progressBar;
    private Spinner leagueSpinner;
    private List<Footballer> footballersList = new ArrayList<>();

    private static final int[] LEAGUE_IDS = {39, 140, 135, 78, 61}; // IDs лиг (например: Премьер-Лига, Ла Лига, Серия A, и т.д.)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_scorers);

        recyclerView = findViewById(R.id.recycler_view_top_scorers);
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
                fetchTopScorers(LEAGUE_IDS[position]); // Запросить бомбардиров для выбранной лиги
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // Изначально загрузим данные для первой лиги (например, Премьер-Лига)
        fetchTopScorers(LEAGUE_IDS[0]);
    }

    private void fetchTopScorers(int leagueId) {
        progressBar.setVisibility(View.VISIBLE);
        Log.d("TopScorersActivity", "🔄 Запрос бомбардиров для лиги с ID: " + leagueId);

        ApiClient.getTopScorers(leagueId, 2024, new ApiResponseCallback() {
            @Override
            public void onSuccess(String jsonData) {
                Log.d("TopScorersActivity", "✅ API ответ: " + jsonData);

                List<Footballer> footballers = ApiClient.parseTopScorers(jsonData);
                Log.d("TopScorersActivity", "📊 Найдено бомбардиров: " + footballers.size());

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (footballers.isEmpty()) {
                        Log.w("TopScorersActivity", "⚠ Нет данных о бомбардирах!");
                        Toast.makeText(TopScorersActivity.this, "Нет данных о бомбардирах", Toast.LENGTH_SHORT).show();
                    } else {
                        footballersList.clear();
                        footballersList.addAll(footballers);
                        adapter = new FootballerAdapter(footballersList, true);
                        recyclerView.setAdapter(adapter);
                        Log.d("TopScorersActivity", "🔄 Данные обновлены в адаптере!");
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(TopScorersActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    Log.e("TopScorersActivity", "❌ Ошибка загрузки бомбардира: " + errorMessage);
                });
            }
        });
    }
}
