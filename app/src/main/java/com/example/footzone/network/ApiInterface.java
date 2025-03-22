package com.example.footzone.network;

import com.example.footzone.model.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface ApiInterface {
    @Headers({
            "x-rapidapi-key: 86cb48a5ba38225946d178801e15af65",
            "x-rapidapi-host: api-football-v1.p.rapidapi.com"
    })
    @GET("v3/news")
    Call<NewsResponse> getFootballNews();
}