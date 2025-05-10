package com.example.footzone.model;

import java.util.List;

public class Match {
    private int id;
    private String date;
    private String homeTeam;
    private String awayTeam;
    private int homeScore;
    private int awayScore;
    private String status;
    private int fixtureId;
    private int homeTeamId;
    private int awayTeamId;
    private String homeTeamLogoUrl;
    private String awayTeamLogoUrl;
    private List<Goal> homeGoals; // Список голов домашней команды
    private List<Goal> awayGoals; // Список голов гостевой команды

    public static class Goal {
        private final String playerName;
        private final int minute;

        public Goal(String playerName, int minute) {
            this.playerName = playerName;
            this.minute = minute;
        }

        public String getPlayerName() {
            return playerName;
        }

        public int getMinute() {
            return minute;
        }
    }

    public Match(String date, String homeTeam, String awayTeam, int homeScore, int awayScore,
                 String status, int fixtureId, int homeTeamId, int awayTeamId,
                 String homeTeamLogoUrl, String awayTeamLogoUrl,
                 List<Goal> homeGoals, List<Goal> awayGoals) {
        this.date = date;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.status = status;
        this.fixtureId = fixtureId;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.homeTeamLogoUrl = homeTeamLogoUrl;
        this.awayTeamLogoUrl = awayTeamLogoUrl;
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDate() { return date; }
    public String getHomeTeam() { return homeTeam; }
    public String getAwayTeam() { return awayTeam; }
    public int getHomeGoals() { return homeScore; }
    public int getAwayGoals() { return awayScore; }
    public String getStatus() { return status; }
    public int getFixtureId() { return fixtureId; }
    public void setFixtureId(int fixtureId) { this.fixtureId = fixtureId; }
    public int getHomeTeamId() { return homeTeamId; }
    public void setHomeTeamId(int homeTeamId) { this.homeTeamId = homeTeamId; }
    public int getAwayTeamId() { return awayTeamId; }
    public void setAwayTeamId(int awayTeamId) { this.awayTeamId = awayTeamId; }
    public String getHomeTeamLogoUrl() { return homeTeamLogoUrl; }
    public void setHomeTeamLogoUrl(String homeTeamLogoUrl) { this.homeTeamLogoUrl = homeTeamLogoUrl; }
    public String getAwayTeamLogoUrl() { return awayTeamLogoUrl; }
    public void setAwayTeamLogoUrl(String awayTeamLogoUrl) { this.awayTeamLogoUrl = awayTeamLogoUrl; }
    public List<Goal> getHomeGoalDetails() { return homeGoals; }
    public void setHomeGoalDetails(List<Goal> homeGoals) { this.homeGoals = homeGoals; }
    public List<Goal> getAwayGoalDetails() { return awayGoals; }
    public void setAwayGoalDetails(List<Goal> awayGoals) { this.awayGoals = awayGoals; }

    public String getFormattedScore() {
        if (status == null || status.equals("NS")) {
            return "-";
        }
        return homeScore + " : " + awayScore;
    }
}