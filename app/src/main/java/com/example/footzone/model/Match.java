package com.example.footzone.model;

public class Match {
    private int id;
    private String date;
    private String homeTeam;
    private String awayTeam;
    private int homeScore;
    private int awayScore;
    private String status;
    private int fixtureId; // Добавляем поле для ID матча

    public Match(String date, String homeTeam, String awayTeam, int homeScore, int awayScore, String status, int fixtureId) {
        this.date = date;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.status = status;
        this.fixtureId = fixtureId;
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
    public int getFixtureId() { return fixtureId; } // Новый геттер
    public void setFixtureId(int fixtureId) { this.fixtureId = fixtureId; }

    public String getFormattedScore() {
        if (status == null || status.equals("NS")) {
            return "-";
        }
        return homeScore + " : " + awayScore;
    }
}