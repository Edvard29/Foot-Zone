package com.example.footzone.network;



import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class ApiClient {
    private static final String API_KEY = "355b9cae7d0abdea74ef9d7034547159";  // Ваш ключ API
    private static OkHttpClient client = new OkHttpClient();

    public static String getMatches(int leagueId) {
        String apiUrl = "https://v3.football.api-sports.io/fixtures?league=" + leagueId + "&season=2023";

        try {
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("x-apisports-key", API_KEY)
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                System.out.println("Ошибка запроса: " + response.code());
                return null;
            }

            ResponseBody responseBody = response.body();
            return responseBody != null ? responseBody.string() : null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    public static String getFootballDataStandings(int leagueId) {
        String apiUrl = "https://v3.football.api-sports.io/standings?league=" + leagueId + "&season=2023";
        try {
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("x-apisports-key", API_KEY)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body() != null ? response.body().string() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFootballNews() {
        String apiUrl = " https://v3.football.api-sports.io/transfers?";
        try {
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("x-apisports-key", API_KEY)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body() != null ? response.body().string() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFootballTransfers(int leagueId, int season) {
        String apiUrl = "https://v3.football.api-sports.io/transfers?league=" + leagueId + "&season=" + season;
        try {
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("x-apisports-key", API_KEY)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body() != null ? response.body().string() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String getTeamSquad(int teamId) {
        String apiUrl = "https://v3.football.api-sports.io/players/squads?team=" + teamId;
        try {
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("x-apisports-key", API_KEY)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body() != null ? response.body().string() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

