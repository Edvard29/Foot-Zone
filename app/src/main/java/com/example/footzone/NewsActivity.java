package com.example.footzone;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footzone.adapter.NewsAdapter;
import com.example.footzone.model.NewsItem;
import com.example.footzone.model.NewsResponse;
import com.example.footzone.network.ApiInterface;
import com.example.footzone.network.ApiClient;
import com.example.footzone.network.FootballApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        recyclerView = findViewById(R.id.recycler_view_news);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new Thread(() -> {
            String jsonNews = ApiClient.getFootballNews(); // Получаем данные с API
            ArrayList<NewsItem> news = FootballApi.parseNews(jsonNews); // Парсим данные
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (news != null && news.size() > 0) {
                        adapter = new NewsAdapter(news, NewsActivity.this);
                        recyclerView.setAdapter(adapter);
                    } else {
                        System.out.println("Новости пустые");
                    }
                }
            });
        }).start();
    }
}