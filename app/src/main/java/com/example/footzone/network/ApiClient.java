package com.example.footzone.network;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.footzone.model.Footballer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

    private static final String API_KEY = "355b9cae7d0abdea74ef9d7034547159";
    private static final OkHttpClient client = new OkHttpClient();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private static final String BASE_URL = "https://v3.football.api-sports.io/";
    private final Map<Integer, String> teamLogoMap = new HashMap<>();

    // Fetch teams and cache logos
    public void fetchTeams(int leagueId, int season, Runnable onSuccess, ApiResponseCallback callback) {
        executorService.execute(() -> {
            try {
                String endpoint = String.format("teams?league=%d&season=%d", leagueId, season);
                Request request = new Request.Builder()
                        .url(BASE_URL + endpoint)
                        .addHeader("x-apisports-key", API_KEY)
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    Log.e("ApiClient", "Error fetching teams: " + response.code());
                    if (callback != null) {
                        callback.onFailure("Error fetching teams: " + response.code());
                    }
                    return;
                }

                ResponseBody responseBody = response.body();
                String result = responseBody != null ? responseBody.string() : null;
                if (result == null || result.isEmpty()) {
                    Log.e("ApiClient", "Empty response from teams API");
                    if (callback != null) {
                        callback.onFailure("Empty response from teams API");
                    }
                    return;
                }

                JSONObject jsonResponse = new JSONObject(result);
                JSONArray teamsArray = jsonResponse.getJSONArray("response");
                teamLogoMap.clear();
                for (int i = 0; i < teamsArray.length(); i++) {
                    JSONObject teamObj = teamsArray.getJSONObject(i).getJSONObject("team");
                    int teamId = teamObj.getInt("id");
                    String logoUrl = teamObj.getString("logo");
                    teamLogoMap.put(teamId, logoUrl);
                }
                Log.d("ApiClient", "Loaded " + teamLogoMap.size() + " team logos");
                if (callback != null) {
                    callback.onSuccess(result);
                }
                if (onSuccess != null) {
                    runOnUiThread(onSuccess);
                }
            } catch (Exception e) {
                Log.e("ApiClient", "Error fetching teams: " + e.getMessage(), e);
                if (callback != null) {
                    callback.onFailure("Error fetching teams: " + e.getMessage());
                }
            }
        });
    }

    @SuppressLint("NewApi")
    public String getTeamLogoUrl(int teamId) {
        return teamLogoMap.getOrDefault(teamId, null);
    }

    // Updated getFixtureEvents to use OkHttpClient
    public static void getFixtureEvents(int fixtureId, ApiResponseCallback callback) {
        String endpoint = "fixtures?ids=" + fixtureId; // Use fixtures endpoint for team logos
        String apiUrl = BASE_URL + endpoint;
        Log.d("ApiClient", "Requesting fixture events: " + apiUrl);
        executeRequest(apiUrl, callback);
    }

    public static void getMatches(int leagueId, ApiResponseCallback callback) {
        String apiUrl = BASE_URL + "fixtures?league=" + leagueId + "&season=2024";
        executeRequest(apiUrl, callback);
    }

    public static void getMatchStatistics(int fixtureId, ApiResponseCallback callback) {
        String apiUrl = BASE_URL + "fixtures/statistics?fixture=" + fixtureId;
        executeRequest(apiUrl, callback);
    }

    public static void getLineups(int fixtureId, ApiResponseCallback callback) {
        String apiUrl = BASE_URL + "fixtures/lineups?fixture=" + fixtureId;
        executeRequest(apiUrl, callback);
    }

    public static String getFootballDataStandings(int leagueId) {
        String apiUrl = BASE_URL + "standings?league=" + leagueId + "&season=2024";
        try {
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("x-apisports-key", API_KEY)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body() != null ? response.body().string() : null;
        } catch (Exception e) {
            Log.e("ApiClient", "Error fetching standings: " + e.getMessage());
            return null;
        }
    }

    public static void getTransfers(int season, ApiResponseCallback callback) {
        String apiUrl = BASE_URL + "transfers?season=" + season;
        executeRequest(apiUrl, callback);
    }

    public static String getTeamSquad(int teamId) {
        String apiUrl = BASE_URL + "players/squads?team=" + teamId;
        try {
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("x-apisports-key", API_KEY)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body() != null ? response.body().string() : null;
        } catch (Exception e) {
            Log.e("ApiClient", "Error fetching squad: " + e.getMessage());
            return null;
        }
    }

    public static String getTeamsByLeague(int leagueId, int season) {
        String apiUrl = BASE_URL + "teams?league=" + leagueId + "&season=" + season;
        try {
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("x-apisports-key", API_KEY)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body() != null ? response.body().string() : null;
        } catch (Exception e) {
            Log.e("ApiClient", "Error fetching teams: " + e.getMessage());
            return null;
        }
    }

    public static void getTopScorers(int leagueId, int seasonYear, ApiResponseCallback callback) {
        String apiUrl = BASE_URL + "players/topscorers?league=" + leagueId + "&season=" + seasonYear;
        executeRequest(apiUrl, callback);
    }

    public static void getAssistants(int leagueId, int seasonYear, ApiResponseCallback callback) {
        String apiUrl = BASE_URL + "players/topassists?league=" + leagueId + "&season=" + seasonYear;
        executeRequest(apiUrl, callback);
    }

    public static void getSquad(int teamId, ApiResponseCallback callback) {
        String apiUrl = BASE_URL + "players/squads?team=" + teamId;
        executeRequest(apiUrl, callback);
    }

    public static void getMatchResult(int fixtureId, ApiResponseCallback callback) {
        String apiUrl = BASE_URL + "fixtures/lineups?fixture=" + fixtureId;
        executeRequest(apiUrl, callback);
    }

    private static void executeRequest(String apiUrl, ApiResponseCallback callback) {
        executorService.execute(() -> {
            try {
                Request request = new Request.Builder()
                        .url(apiUrl)
                        .addHeader("x-apisports-key", API_KEY)
                        .build();
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    Log.e("ApiClient", "API error: " + response.code());
                    callback.onFailure("API request failed with code: " + response.code());
                    return;
                }
                ResponseBody responseBody = response.body();
                String result = responseBody != null ? responseBody.string() : null;
                if (result != null && !result.isEmpty()) {
                    Log.d("ApiClient", "API response: " + result.substring(0, Math.min(result.length(), 100)));
                    callback.onSuccess(result);
                } else {
                    Log.e("ApiClient", "Empty response from API");
                    callback.onFailure("Empty response from API");
                }
            } catch (IOException e) {
                Log.e("ApiClient", "Network error: " + e.getMessage());
                callback.onFailure("Network error: " + e.getMessage());
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
                String imageUrl = player.optString("photo", "");
                int goalCount = goals.optInt("total", 0);
                int assistCount = goals.optInt("assists", 0);

                Log.d("ApiClient", "Player: " + name + " | Team: " + teamName + " | Goals: " + goalCount + " | Assists: " + assistCount);
                scorers.add(new Footballer(name, goalCount, assistCount, imageUrl));
            }
        } catch (JSONException e) {
            Log.e("ApiClient", "Error parsing top scorers: " + e.getMessage());
        }
        return scorers;
    }

    public static List<Footballer> parseAssistants(String jsonData) {
        List<Footballer> assistLeaders = new ArrayList<>();
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
                String imageUrl = player.optString("photo", "");
                int assistCount = goals.optInt("assists", 0);
                int goalCount = goals.optInt("total", 0);

                Log.d("ApiClient", "Player: " + name + " | Team: " + teamName + " | Goals: " + goalCount + " | Assists: " + assistCount);
                assistLeaders.add(new Footballer(name, goalCount, assistCount, imageUrl));
            }
        } catch (JSONException e) {
            Log.e("ApiClient", "Error parsing assist leaders: " + e.getMessage());
        }
        return assistLeaders;
    }

    // Helper to run on UI thread
    private static void runOnUiThread(Runnable runnable) {
        new android.os.Handler(android.os.Looper.getMainLooper()).post(runnable);
    }
}