package com.example.footzone.model;

public class Prediction {
    private String userId;
    private int fixtureId;
    private int homeGoals;
    private int awayGoals;
    private String homeLineup;
    private String awayLineup;
    private long timestamp;

    public Prediction() {
        // Пустой конструктор для Firebase
    }

    public Prediction(String userId, int fixtureId, int homeGoals, int awayGoals, String homeLineup, String awayLineup, long timestamp) {
        this.userId = userId;
        this.fixtureId = fixtureId;
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
        this.homeLineup = homeLineup;
        this.awayLineup = awayLineup;
        this.timestamp = timestamp;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public int getFixtureId() { return fixtureId; }
    public void setFixtureId(int fixtureId) { this.fixtureId = fixtureId; }
    public int getHomeGoals() { return homeGoals; }
    public void setHomeGoals(int homeGoals) { this.homeGoals = homeGoals; }
    public int getAwayGoals() { return awayGoals; }
    public void setAwayGoals(int awayGoals) { this.awayGoals = awayGoals; }
    public String getHomeLineup() { return homeLineup; }
    public void setHomeLineup(String homeLineup) { this.homeLineup = homeLineup; }
    public String getAwayLineup() { return awayLineup; }
    public void setAwayLineup(String awayLineup) { this.awayLineup = awayLineup; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}