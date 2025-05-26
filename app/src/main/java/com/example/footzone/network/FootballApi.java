package com.example.footzone.network;

import android.util.Log;
import com.example.footzone.model.Match;
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
            Log.d("FootballApi", "Received " + responseArray.length() + " matches in response");

            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject fixture = responseArray.getJSONObject(i);
                JSONObject fixtureData = fixture.optJSONObject("fixture");
                JSONObject teams = fixture.optJSONObject("teams");
                JSONObject goals = fixture.optJSONObject("goals");
                JSONArray events = fixture.optJSONArray("events");

                if (fixtureData == null || teams == null || goals == null) {
                    Log.w("FootballApi", "Skipped match " + i + ": missing fixture, teams, or goals data");
                    continue;
                }

                String date = fixtureData.optString("date", "");
                String status = fixtureData.optJSONObject("status") != null
                        ? fixtureData.getJSONObject("status").optString("short", "NS")
                        : "NS";
                int fixtureId = fixtureData.optInt("id", -1);

                JSONObject homeTeam = teams.optJSONObject("home");
                JSONObject awayTeam = teams.optJSONObject("away");

                if (homeTeam == null || awayTeam == null) {
                    Log.w("FootballApi", "Skipped match ID " + fixtureId + ": missing team data");
                    continue;
                }

                String homeTeamName = homeTeam.optString("name", "Unknown Team");
                int homeTeamId = homeTeam.optInt("id", -1);
                String homeTeamLogo = homeTeam.optString("logo", "");

                String awayTeamName = awayTeam.optString("name", "Unknown Team");
                int awayTeamId = awayTeam.optInt("id", -1);
                String awayTeamLogo = awayTeam.optString("logo", "");

                int homeScore = goals.isNull("home") ? 0 : goals.optInt("home", 0);
                int awayScore = goals.isNull("away") ? 0 : goals.optInt("away", 0);

                // Parse goals (likely empty in /fixtures response)
                List<Match.Goal> homeGoals = new ArrayList<>();
                List<Match.Goal> awayGoals = new ArrayList<>();
                if (events != null && events.length() > 0) {
                    Log.d("FootballApi", "Match ID " + fixtureId + ": found " + events.length() + " events");
                    for (int j = 0; j < events.length(); j++) {
                        JSONObject event = events.optJSONObject(j);
                        if (event == null) {
                            Log.w("FootballApi", "Match ID " + fixtureId + ": event " + j + " is null");
                            continue;
                        }

                        String eventType = event.optString("type", "").trim();
                        String detail = event.optString("detail", "").trim();
                        Log.d("FootballApi", "Event " + j + ": type=" + eventType + ", detail=" + detail);

                        if (eventType.equalsIgnoreCase("Goal")) {
                            JSONObject time = event.optJSONObject("time");
                            int minute = time != null ? time.optInt("elapsed", 0) : 0;
                            JSONObject player = event.optJSONObject("player");
                            String playerName = player != null ? player.optString("name", "Unknown Player") : "Unknown Player";
                            JSONObject team = event.optJSONObject("team");
                            String teamName = team != null ? team.optString("name", "") : "";
                            boolean isOwnGoal = detail.equalsIgnoreCase("Own Goal");
                            String goalType = detail.isEmpty() ? "Normal Goal" : detail;

                            Log.d("FootballApi", "Goal: player=" + playerName + ", minute=" + minute +
                                    ", team=" + teamName + ", isOwnGoal=" + isOwnGoal + ", type=" + goalType);

                            Match.Goal goal = new Match.Goal(playerName, minute, isOwnGoal, goalType);
                            if (teamName.equals(homeTeamName)) {
                                homeGoals.add(goal);
                            } else if (teamName.equals(awayTeamName)) {
                                awayGoals.add(goal);
                            } else {
                                Log.w("FootballApi", "Match ID " + fixtureId + ": goal not assigned, teamName=" + teamName);
                            }
                        }
                    }
                } else {
                    Log.w("FootballApi", "Match ID " + fixtureId + ": events array is missing or empty");
                    if (homeScore > 0 || awayScore > 0) {
                        Log.e("FootballApi", "Match ID " + fixtureId + ": goals reported (home=" + homeScore +
                                ", away=" + awayScore + ") but no events found");
                    }
                }

                Log.d("FootballApi", "Match ID " + fixtureId + ": Home goals=" + homeGoals.size() +
                        ", Away goals=" + awayGoals.size());

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
                        homeTeamLogo,
                        homeGoals,
                        awayGoals
                );
                matches.add(match);
            }
            Log.d("FootballApi", "Successfully parsed " + matches.size() + " matches");
        } catch (Exception e) {
            Log.e("FootballApi", "Error parsing matches: " + e.getMessage(), e);
        }
        return matches;
    }

    public static void parseMatchEvents(String jsonData, Match match) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray responseArray = jsonObject.getJSONArray("response");
            Log.d("FootballApi", "Received " + responseArray.length() + " events for match ID " + match.getFixtureId());

            List<Match.Goal> homeGoals = new ArrayList<>();
            List<Match.Goal> awayGoals = new ArrayList<>();

            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject event = responseArray.optJSONObject(i);
                if (event == null) {
                    Log.w("FootballApi", "Match ID " + match.getFixtureId() + ": event " + i + " is null");
                    continue;
                }

                String eventType = event.optString("type", "").trim();
                String detail = event.optString("detail", "").trim();
                Log.d("FootballApi", "Event " + i + ": type=" + eventType + ", detail=" + detail);

                if (eventType.equalsIgnoreCase("Goal")) {
                    JSONObject time = event.optJSONObject("time");
                    int minute = time != null ? time.optInt("elapsed", 0) : 0;
                    JSONObject player = event.optJSONObject("player");
                    String playerName = player != null ? player.optString("name", "Unknown Player") : "Unknown Player";
                    JSONObject team = event.optJSONObject("team");
                    String teamName = team != null ? team.optString("name", "") : "";
                    boolean isOwnGoal = detail.equalsIgnoreCase("Own Goal");
                    String goalType = detail.isEmpty() ? "Normal Goal" : detail;

                    Log.d("FootballApi", "Goal: player=" + playerName + ", minute=" + minute +
                            ", team=" + teamName + ", isOwnGoal=" + isOwnGoal + ", type=" + goalType);

                    Match.Goal goal = new Match.Goal(playerName, minute, isOwnGoal, goalType);
                    if (teamName.equals(match.getHomeTeam())) {
                        homeGoals.add(goal);
                    } else if (teamName.equals(match.getAwayTeam())) {
                        awayGoals.add(goal);
                    } else {
                        Log.w("FootballApi", "Match ID " + match.getFixtureId() + ": goal not assigned, teamName=" + teamName);
                    }
                }
            }

            match.setHomeGoalDetails(homeGoals);
            match.setAwayGoalDetails(awayGoals);
            Log.d("FootballApi", "Updated match ID " + match.getFixtureId() + ": Home goals=" + homeGoals.size() +
                    ", Away goals=" + awayGoals.size());
        } catch (Exception e) {
            Log.e("FootballApi", "Error parsing match events for match ID " + match.getFixtureId() + ": " + e.getMessage(), e);
        }
    }
}