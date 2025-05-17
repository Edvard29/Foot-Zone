package com.example.footzone.network;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.footzone.model.Footballer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static final String BASE_URL = "https://v3.football.api-sports.io/";
    private final Map<Integer, String> teamLogoMap = new HashMap<>();

    public void fetchTeams(int leagueId, int season, Runnable onSuccess) {
        new Thread(() -> {
            try {
                String endpoint = String.format("teams?league=%d&season=%d", leagueId, season);
                URL url = new URL(BASE_URL + endpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("x-rapidapi-key", API_KEY);
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray teamsArray = jsonResponse.getJSONArray("response");
                    teamLogoMap.clear();
                    for (int i = 0; i < teamsArray.length(); i++) {
                        JSONObject teamObj = teamsArray.getJSONObject(i).getJSONObject("team");
                        int teamId = teamObj.getInt("id");
                        String logoUrl = teamObj.getString("logo");
                        teamLogoMap.put(teamId, logoUrl);
                    }
                    Log.d("ApiFootballService", "Загружено " + teamLogoMap.size() + " логотипов команд");
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                } else {
                    Log.e("ApiFootballService", "Ошибка API: код " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e("ApiFootballService", "Ошибка при загрузке команд: " + e.getMessage(), e);
            }
        }).start();
    }

    @SuppressLint("NewApi")
    public String getTeamLogoUrl(int teamId) {
        return teamLogoMap.getOrDefault(teamId, null);
    }
    public static String getFixtureEvents(int fixtureId, ApiResponseCallback apiResponseCallback) {
        String endpoint = "fixtures/events?fixture=" + fixtureId;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(BASE_URL + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("x-rapidapi-key", API_KEY);
            connection.setRequestProperty("x-rapidapi-host", "api-football-v1.p.rapidapi.com");
            connection.setConnectTimeout(5000); // 5 секунд таймаут
            connection.setReadTimeout(5000);    // 5 секунд таймаут

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                Log.d(TAG, "Успешный запрос к " + endpoint + ": " + response.toString().substring(0, Math.min(1000, response.length())));
                return response.toString();
            } else {
                Log.e(TAG, "Ошибка запроса к " + endpoint + ": Код " + responseCode);
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Исключение при запросе к " + endpoint + ": " + e.getMessage(), e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }



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




    public static void getTransfers(int season, ApiResponseCallback callback) {
        String apiUrl = BASE_URL + "transfers?season=" + season;
        Log.d("ApiClient", "📡 Запрос трансферов: " + apiUrl);

        executorService.execute(() -> {
            try {
                Request request = new Request.Builder()
                        .url(apiUrl)
                        .addHeader("x-apisports-key", API_KEY)
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    Log.e("ApiClient", "❌ Ошибка API (трансферы): " + response.code());
                    callback.onFailure("Ошибка запроса: " + response.code());
                    return;
                }

                ResponseBody responseBody = response.body();
                String result = responseBody != null ? responseBody.string() : null;
                if (result != null && !result.isEmpty()) {
                    Log.d("ApiClient", "✅ Ответ API (трансферы): " + result.substring(0, Math.min(result.length(), 100)));
                    callback.onSuccess(result);
                } else {
                    Log.e("ApiClient", "❌ Пустой ответ от API (трансферы)");
                    callback.onFailure("Пустой ответ от API");
                }
            } catch (IOException e) {
                Log.e("ApiClient", "❌ Ошибка сети (трансферы): " + e.getMessage());
                callback.onFailure("Ошибка сети: " + e.getMessage());
            }
        });
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

            Log.d("ApiClient", "📜 Full JSON (Top Scorers): " + jsonData);

            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject playerObject = responseArray.getJSONObject(i);
                JSONObject player = playerObject.getJSONObject("player");
                JSONObject statistics = playerObject.getJSONArray("statistics").getJSONObject(0);
                JSONObject team = statistics.getJSONObject("team");
                JSONObject goals = statistics.getJSONObject("goals");

                String name = player.getString("name");
                String teamName = team.getString("name");
                String imageUrl = player.optString("photo", ""); // Get photo URL, default to empty string
                int goalCount = goals.optInt("total", 0);
                int assistCount = goals.optInt("assists", 0);

                Log.d("ApiClient", "👤 Player: " + name + " | Team: " + teamName + " | Goals: " + goalCount + " | Assists: " + assistCount + " | Image: " + imageUrl);

                scorers.add(new Footballer(name,  goalCount, assistCount, imageUrl));
            }
        } catch (JSONException e) {
            Log.e("ApiClient", "❌ Error parsing top scorers: " + e.getMessage());
            e.printStackTrace();
        }

        return scorers;
    }


    public static List<Footballer> parseAssistants(String jsonData) {
        List<Footballer> assistLeaders = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray responseArray = jsonObject.getJSONArray("response");

            Log.d("ApiClient", "📜 Full JSON (Assist Leaders): " + jsonData);

            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject playerObject = responseArray.getJSONObject(i);
                JSONObject player = playerObject.getJSONObject("player");
                JSONObject statistics = playerObject.getJSONArray("statistics").getJSONObject(0);
                JSONObject team = statistics.getJSONObject("team");
                JSONObject goals = statistics.getJSONObject("goals");

                String name = player.getString("name");
                String teamName = team.getString("name");
                String imageUrl = player.optString("photo", ""); // Get photo URL, default to empty string
                int assistCount = goals.optInt("assists", 0);
                int goalCount = goals.optInt("total", 0);

                Log.d("ApiClient", "👤 Player: " + name + " | Team: " + teamName + " | Goals: " + goalCount + " | Assists: " + assistCount + " | Image: " + imageUrl);

                assistLeaders.add(new Footballer(name,  goalCount, assistCount, imageUrl));
            }
        } catch (JSONException e) {
            Log.e("ApiClient", "❌ Error parsing assist leaders: " + e.getMessage());
            e.printStackTrace();
        }

        return assistLeaders;
    }
    public static void getSquad(int teamId, ApiResponseCallback callback) {
        String apiUrl = BASE_URL + "players/squads?team=" + teamId;
        Log.d("ApiClient", "📡 Requesting squad: " + apiUrl);
        executeRequest(apiUrl, callback);
    }

    /**
     * Получение результатов матча и стартового состава
     * Эндпоинт: /fixtures/lineups
     */
    public static void getMatchResult(int fixtureId, ApiResponseCallback callback) {
        String apiUrl = BASE_URL + "fixtures/lineups?fixture=" + fixtureId;
        Log.d("ApiClient", "📡 Requesting match result: " + apiUrl);
        executeRequest(apiUrl, callback);
    }



    /**
     * Выполнение HTTP-запроса
     */
    private static void executeRequest(String apiUrl, ApiResponseCallback callback) {
        executorService.execute(() -> {
            try {
                Request request = new Request.Builder()
                        .url(apiUrl)
                        .addHeader("x-apisports-key", API_KEY)
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    Log.e("ApiClient", "❌ API error: " + response.code());
                    callback.onFailure("API request failed with code: " + response.code());
                    return;
                }

                ResponseBody responseBody = response.body();
                String result = responseBody != null ? responseBody.string() : null;
                if (result != null && !result.isEmpty()) {
                    Log.d("ApiClient", "✅ API response: " + result.substring(0, Math.min(result.length(), 100)));
                    callback.onSuccess(result);
                } else {
                    Log.e("ApiClient", "❌ Empty response from API");
                    callback.onFailure("Empty response from API");
                }
            } catch (IOException e) {
                Log.e("ApiClient", "❌ Network error: " + e.getMessage());
                callback.onFailure("Network error: " + e.getMessage());
            }
        });
    }




}
