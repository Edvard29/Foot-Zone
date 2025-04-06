package com.example.footzone.network;

import android.util.Log;

import com.example.footzone.model.Footballer;
import com.example.footzone.model.Match;
import com.example.footzone.model.Transfer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ApiClient {
    private static final String API_KEY = "355b9cae7d0abdea74ef9d7034547159";  // Ваш ключ API
    private static OkHttpClient client = new OkHttpClient();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(3);

    public static void getMatches(int leagueId, ApiResponseCallback callback) {
        executorService.execute(() -> {
            String apiUrl = "https://v3.football.api-sports.io/fixtures?league=" + leagueId + "&season=2024";

            try {
                Request request = new Request.Builder()
                        .url(apiUrl)
                        .addHeader("x-apisports-key", API_KEY)
                        .build();

                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    callback.onFailure("Ошибка запроса: " + response.code());
                    return;
                }

                ResponseBody responseBody = response.body();
                String result = responseBody != null ? responseBody.string() : null;

                callback.onSuccess(result);
            } catch (IOException e) {
                callback.onFailure("Ошибка сети: " + e.getMessage());
            }
        });
    }
    public static void getMatchStatistics(int fixtureId, ApiResponseCallback callback) {
        executorService.execute(() -> {
            String apiUrl = "https://v3.football.api-sports.io/fixtures/statistics?fixture=" + fixtureId;
            try {
                Request request = new Request.Builder()
                        .url(apiUrl)
                        .addHeader("x-apisports-key", API_KEY)
                        .build();
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    callback.onFailure("Ошибка запроса: " + response.code());
                    return;
                }
                ResponseBody responseBody = response.body();
                String result = responseBody != null ? responseBody.string() : null;
                callback.onSuccess(result);
            } catch (IOException e) {
                callback.onFailure("Ошибка сети: " + e.getMessage());
            }
        });
    }


    public static void getLineups(int fixtureId, ApiResponseCallback callback) {
        executorService.execute(() -> {
            String apiUrl = "https://v3.football.api-sports.io/fixtures/lineups?fixture=" + fixtureId;
            try {
                Request request = new Request.Builder()
                        .url(apiUrl)
                        .addHeader("x-apisports-key", API_KEY)
                        .build();
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    callback.onFailure("Ошибка запроса: " + response.code());
                    return;
                }
                ResponseBody responseBody = response.body();
                String result = responseBody != null ? responseBody.string() : null;
                callback.onSuccess(result);
            } catch (IOException e) {
                callback.onFailure("Ошибка сети: " + e.getMessage());
            }
        });
    }


    public static String getFootballDataStandings(int leagueId) {
        String apiUrl = "https://v3.football.api-sports.io/standings?league=" + leagueId + "&season=2024";
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


    public static String getTransfers(int playerId, int teamId, ApiResponseCallback callback) throws Exception {
        // Твой код для выполнения запроса
        String url = "https://api-football-v1.p.rapidapi.com/v3/transfers?player=" + playerId + "&team=" + teamId;


        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("X-RapidAPI-Key", "YOUR_API_KEY")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("Ошибка запроса: " + response.code());
            }
            return response.body().string(); // Возвращаем строку с ответом
        }
    }




    public static void parseTransfers(String responseBody, ApiResponseCallback callback) {
        try {
            JSONObject responseObject = new JSONObject(responseBody);
            JSONArray transfersArray = responseObject.getJSONArray("response");

            List<Transfer> transfers = new ArrayList<>();

            for (int i = 0; i < transfersArray.length(); i++) {
                JSONObject transferObject = transfersArray.getJSONObject(i);

                JSONObject playerObject = transferObject.getJSONObject("player");
                String playerName = playerObject.getString("name");

                JSONObject transferDetails = transferObject.getJSONObject("transfer");
                String transferDate = transferDetails.optString("date", "N/A");
                String fromTeam = transferDetails.optString("from_team", "N/A");
                String toTeam = transferDetails.optString("to_team", "N/A");

                Transfer transfer = new Transfer(playerName, fromTeam, toTeam, transferDate);
                transfers.add(transfer);
            }

            callback.onSuccess(transfers.toString());
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure("Ошибка при разборе данных: " + e.getMessage());
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

    // Получение команд по лиге и сезону
    public static String getTeamsByLeague(int leagueId, int season) {
        String apiUrl = "https://v3.football.api-sports.io/teams?league=" + leagueId + "&season=" + season;
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


    public static void getTopScorers(int leagueId, int seasonYear, ApiResponseCallback callback) {
        String apiUrl = "https://v3.football.api-sports.io/players/topscorers?league=" + leagueId + "&season=" + seasonYear;

        executeApiRequest(apiUrl, callback); // Используем executeApiRequest
    }

    public static void getAssistants(int leagueId, int seasonYear, ApiResponseCallback callback) {
        String apiUrl = "https://v3.football.api-sports.io/players/topassists?league=" + leagueId + "&season=" + seasonYear;

        Log.d("ApiClient", "📡 Запрос ассистентов: " + apiUrl);

        executeApiRequest(apiUrl, new ApiResponseCallback() {
            @Override
            public void onSuccess(String jsonData) {
                Log.d("ApiClient", "✅ Ответ API (ассистенты): " + jsonData);
                callback.onSuccess(jsonData);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("ApiClient", "❌ Ошибка API (ассистенты): " + errorMessage);
                callback.onFailure(errorMessage);
            }
        });
    }

    private static void executeApiRequest(String apiUrl, ApiResponseCallback callback) {
        executorService.execute(() -> {
            try {
                Request request = new Request.Builder()
                        .url(apiUrl)
                        .addHeader("x-apisports-key", API_KEY)
                        .build();

                Response response = client.newCall(request).execute();
                String responseBody = response.body() != null ? response.body().string() : null;

                if (responseBody != null && !responseBody.isEmpty()) {
                    callback.onSuccess(responseBody);
                } else {
                    callback.onFailure("Пустой ответ от API");
                }

            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure(e.getMessage());
            }
        });
    }

    public static List<Footballer> parseTopScorers(String jsonData) {
        List<Footballer> scorers = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray responseArray = jsonObject.getJSONArray("response");

            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject playerObject = responseArray.getJSONObject(i);
                JSONObject player = playerObject.getJSONObject("player");
                JSONObject statistics = playerObject.getJSONArray("statistics").getJSONObject(0);
                JSONObject team = statistics.getJSONObject("team");
                JSONObject goals = statistics.getJSONObject("goals");

                String name = player.getString("name");
                String teamName = team.getString("name");
                int goalCount = goals.optInt("total", 0);  // Количество голов
                int assistCount = goals.optInt("assists", 0);  // Количество ассистов

                scorers.add(new Footballer(name, teamName, goalCount, assistCount));  // Добавляем футболиста
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return scorers;
    }



    public static List<Footballer> parseAssistants(String jsonData) {
        List<Footballer> assistLeaders = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray responseArray = jsonObject.getJSONArray("response");

            Log.d("ApiClient", "📜 Полный JSON: " + jsonData); // Логируем полный JSON

            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject playerObject = responseArray.getJSONObject(i);
                JSONObject player = playerObject.getJSONObject("player");
                JSONObject statistics = playerObject.getJSONArray("statistics").getJSONObject(0);
                JSONObject team = statistics.getJSONObject("team");
                JSONObject goals = statistics.getJSONObject("goals");

                String name = player.getString("name");
                String teamName = team.getString("name");
                int assistCount = goals.optInt("assists", 0);  // ✅ Теперь берем ассисты из goals

                Log.d("ApiClient", "👤 Игрок: " + name + " | Команда: " + teamName + " | Ассисты: " + assistCount);

                assistLeaders.add(new Footballer(name, teamName, 0, assistCount)); // 0 голов, ассисты есть
            }

        } catch (JSONException e) {
            Log.e("ApiClient", "❌ Ошибка парсинга ассистентов: " + e.getMessage());
            e.printStackTrace();
        }

        return assistLeaders;
    }
}
