package com.example.footzone.network;

import android.util.Log;
import com.example.footzone.model.Match;
import com.example.footzone.model.TeamStanding;
import com.example.footzone.model.Transfer;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class FootballApi {
    public static List<Match> parseMatches(String jsonData) {
        List<Match> matches = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray responseArray = jsonObject.getJSONArray("response");
            Log.d("FootballApi", "Получено " + responseArray.length() + " матчей в ответе");

            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject fixture = responseArray.getJSONObject(i);
                JSONObject fixtureData = fixture.getJSONObject("fixture");
                JSONObject teams = fixture.getJSONObject("teams");
                JSONObject goals = fixture.getJSONObject("goals");
                JSONArray events = fixture.optJSONArray("events");

                String date = fixtureData.getString("date");
                String status = fixtureData.getJSONObject("status").getString("short");
                int fixtureId = fixtureData.getInt("id");

                JSONObject homeTeam = teams.getJSONObject("home");
                JSONObject awayTeam = teams.getJSONObject("away");

                String homeTeamName = homeTeam.getString("name");
                int homeTeamId = homeTeam.getInt("id");
                String homeTeamLogo = homeTeam.getString("logo");

                String awayTeamName = awayTeam.getString("name");
                int awayTeamId = awayTeam.getInt("id");
                String awayTeamLogo = awayTeam.getString("logo");

                int homeScore = goals.isNull("home") ? 0 : goals.getInt("home");
                int awayScore = goals.isNull("away") ? 0 : goals.getInt("away");

                // Парсинг голов
                List<Match.Goal> homeGoals = new ArrayList<>();
                List<Match.Goal> awayGoals = new ArrayList<>();
                if (events != null) {
                    Log.d("FootballApi", "Матч ID " + fixtureId + ": найдено " + events.length() + " событий");
                    for (int j = 0; j < events.length(); j++) {
                        JSONObject event = events.getJSONObject(j);
                        String eventType = event.optString("type", "");
                        String detail = event.optString("detail", "");
                        Log.d("FootballApi", "Событие " + j + ": type=" + eventType + ", detail=" + detail);

                        if (eventType.equals("Goal") && !detail.equals("Own Goal")) {
                            JSONObject time = event.optJSONObject("time");
                            int minute = time != null ? time.optInt("elapsed", 0) : 0;
                            JSONObject player = event.optJSONObject("player");
                            String playerName = player != null ? player.optString("name", "Неизвестный игрок") : "Неизвестный игрок";
                            JSONObject team = event.optJSONObject("team");
                            int teamId = team != null ? team.optInt("id", -1) : -1;

                            Log.d("FootballApi", "Гол: игрок=" + playerName + ", минута=" + minute + ", teamId=" + teamId);

                            if (teamId == homeTeamId) {
                                homeGoals.add(new Match.Goal(playerName, minute));
                            } else if (teamId == awayTeamId) {
                                awayGoals.add(new Match.Goal(playerName, minute));
                            }
                        }
                    }
                } else {
                    Log.w("FootballApi", "Матч ID " + fixtureId + ": массив events отсутствует или пуст");
                }

                Log.d("FootballApi", "Матч ID " + fixtureId + ": Домашние голы=" + homeGoals.size() + ", Гостевые голы=" + awayGoals.size());

                Match match = new Match(
                        date,
                        homeTeamName,
                        awayTeamName,
                        homeScore,
                        awayScore,
                        status,
                        fixtureId,
                        homeTeamId,
                        awayTeamId,
                        homeTeamLogo,
                        awayTeamLogo,
                        homeGoals,
                        awayGoals
                );
                matches.add(match);
            }
            Log.d("FootballApi", "Успешно спарсено " + matches.size() + " матчей");
        } catch (Exception e) {
            Log.e("FootballApi", "Ошибка парсинга матчей: " + e.getMessage(), e);
        }
        return matches;
    }
}