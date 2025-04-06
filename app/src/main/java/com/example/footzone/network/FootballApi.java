package com.example.footzone.network;

import android.util.Log;

import com.example.footzone.model.TeamStanding;
import com.example.footzone.model.Match;
import com.example.footzone.model.Transfer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class FootballApi {
    public static List<Match> parseMatches(String jsonData) {
        List<Match> matches = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray responseArray = jsonObject.getJSONArray("response");

            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject matchObject = responseArray.getJSONObject(i);
                JSONObject teams = matchObject.getJSONObject("teams");
                JSONObject goals = matchObject.getJSONObject("goals");
                JSONObject score = matchObject.getJSONObject("score");

                // Получаем дату и ID матча
                JSONObject fixture = matchObject.getJSONObject("fixture");
                String date = fixture.getString("date");
                int fixtureId = fixture.getInt("id"); // <-- добавлено

                String homeTeam = teams.getJSONObject("home").getString("name");
                String awayTeam = teams.getJSONObject("away").getString("name");

                int homeGoals = goals.isNull("home") ? 0 : goals.getInt("home");
                int awayGoals = goals.isNull("away") ? 0 : goals.getInt("away");

                if (score.has("fulltime") && !score.isNull("fulltime")) {
                    JSONObject fulltime = score.getJSONObject("fulltime");
                    homeGoals = fulltime.isNull("home") ? homeGoals : fulltime.getInt("home");
                    awayGoals = fulltime.isNull("away") ? awayGoals : fulltime.getInt("away");
                }

                String status = fixture.getJSONObject("status").getString("short");

                Match match = new Match(date, homeTeam, awayTeam, homeGoals, awayGoals, status, fixtureId);
                matches.add(match);
            }
        } catch (Exception e) {
            Log.e("FootballApi", "Error parsing matches", e);
        }
        return matches;
    }


    public static ArrayList<TeamStanding> parseStandings(String jsonData) {
        ArrayList<TeamStanding> standings = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray response = jsonObject.getJSONArray("response");

            for (int j = 0; j < response.length(); j++) {
                JSONObject leagueObj = response.getJSONObject(j);
                JSONObject league = leagueObj.getJSONObject("league");
                String leagueName = league.getString("name");
                JSONArray standingsArray = league.getJSONArray("standings").getJSONArray(0);

                // Добавляем информацию о лиге только один раз
                if (j == 0) {
                    standings.add(new TeamStanding("League: " + leagueName, true, 0)); // Заголовок лиги
                }

                // Перебираем список команд в лиге
                for (int i = 0; i < standingsArray.length(); i++) {
                    JSONObject teamObj = standingsArray.getJSONObject(i);
                    JSONObject team = teamObj.getJSONObject("team");
                    String teamName = team.getString("name");
                    int points = teamObj.getInt("points");

                    // Добавляем каждую команду
                    standings.add(new TeamStanding(teamName, false, points));
                }
            }
        } catch (Exception e) {
            Log.e("FootballApi", "Error parsing standings", e);
        }
        return standings;
    }




    public static List<Transfer> parseTransfers(String jsonData) {
        List<Transfer> transfers = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray transferArray = jsonObject.getJSONArray("transfers");

            for (int i = 0; i < transferArray.length(); i++) {
                JSONObject transferObj = transferArray.getJSONObject(i);
                String playerName = transferObj.getString("player_name");
                String fromTeam = transferObj.getString("from_team");
                String toTeam = transferObj.getString("to_team");
                String transferDate = transferObj.getString("transfer_date");

                transfers.add(new Transfer(playerName, fromTeam, toTeam, transferDate));
            }
        } catch (JSONException e) {
            Log.e("ApiClient", "Error parsing transfers", e);
        }
        return transfers;
    }
}
