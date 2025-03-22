package com.example.footzone.network;



import android.util.Log;

import com.example.footzone.model.NewsItem;
import com.example.footzone.model.TeamStanding;
import com.example.footzone.model.Match;
import com.example.footzone.model.TransferItem;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

import java.util.Collections;

import java.util.Comparator;
public class FootballApi {
    public static ArrayList<Match> parseMatches(String jsonData) {
        ArrayList<Match> matches = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray response = jsonObject.optJSONArray("response");

            Log.d("ParseMatches", "Response Length: " + response.length());  // Логируем количество матчей

            if (response != null) {
                for (int i = 0; i < Math.min(50, response.length()); i++) {
                    JSONObject matchObj = response.optJSONObject(i).optJSONObject("fixture");
                    if (matchObj != null) {
                        String date = matchObj.optString("date", "Unknown Date");
                        JSONObject teams = response.optJSONObject(i).optJSONObject("teams");
                        String home = teams != null ? teams.optJSONObject("home").optString("name", "Unknown Team") : "Unknown Team";
                        String away = teams != null ? teams.optJSONObject("away").optString("name", "Unknown Team") : "Unknown Team";
                        JSONObject goals = response.optJSONObject(i).optJSONObject("goals");
                        String homeGoals = goals != null && !goals.isNull("home") ? String.valueOf(goals.optInt("home", 0)) : "-";
                        String awayGoals = goals != null && !goals.isNull("away") ? String.valueOf(goals.optInt("away", 0)) : "-";

                        // Логируем данные каждого матча
                        Log.d("MatchData", "Match " + i + ": " + home + " vs " + away + " | " + date);

                        // Добавляем матч в список
                        matches.add(new Match(date, home, away, homeGoals, awayGoals));
                    }
                }
            }

            // Сортировка матчей по дате
            Collections.sort(matches, new Comparator<Match>() {
                @Override
                public int compare(Match m1, Match m2) {
                    return m1.getDate().compareTo(m2.getDate());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();  // Логируем исключения для отладки
        }
        return matches;
    }



    public static ArrayList<TeamStanding> parseStandings(String jsonData) {
        ArrayList<TeamStanding> standings = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray response = jsonObject.getJSONArray("response");

            if (response.length() > 0) {
                JSONObject league = response.getJSONObject(0).getJSONObject("league");
                JSONArray standingsArray = league.getJSONArray("standings").getJSONArray(0);

                for (int i = 0; i < standingsArray.length(); i++) {
                    JSONObject teamObj = standingsArray.getJSONObject(i);
                    JSONObject team = teamObj.getJSONObject("team");
                    String teamName = team.getString("name");
                    int points = teamObj.getInt("points");

                    standings.add(new TeamStanding(teamName, points));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return standings;
    }

    public static ArrayList<NewsItem> parseNews(String jsonData) {
        ArrayList<NewsItem> newsList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray articles = jsonObject.getJSONArray("response");

            for (int i = 0; i < articles.length(); i++) {
                JSONObject article = articles.getJSONObject(i);
                String title = article.getString("title");
                String description = article.getString("description");

                newsList.add(new NewsItem(title, description));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newsList;
    }

    public static ArrayList<TransferItem> parseTransfers(String jsonData) {
        ArrayList<TransferItem> transferList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray transfers = jsonObject.getJSONArray("response");

            for (int i = 0; i < transfers.length(); i++) {
                JSONObject transfer = transfers.getJSONObject(i);
                String player = transfer.getJSONObject("player").getString("name");
                String fromTeam = transfer.getJSONObject("teams").getJSONObject("out").getString("name");
                String toTeam = transfer.getJSONObject("teams").getJSONObject("in").getString("name");

                transferList.add(new TransferItem(player, fromTeam, toTeam));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transferList;
    }



}
