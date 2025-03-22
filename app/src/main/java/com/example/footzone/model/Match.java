package com.example.footzone.model;

public class Match {
    private int id;
    private String date;
    private String homeTeam;
    private String awayTeam;
    private int homeScore;
    private int awayScore;
    private String status;

    // Конструктор с шестью параметрами
    public Match(String homeTeam, String awayTeam, String date, int homeScore, int awayScore, String status) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.date = date;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.status = status;
    }

    // ✅ Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public int getHomeGoals() {
        return homeScore;
    }

    public int getAwayGoals() {
        return awayScore;
    }

    public String getStatus() {
        return status;
    }

    // ✅ Форматированный счёт
    public String getFormattedScore() {
        if (status == null || status.equals("NS")) {
            return "-";
        }
        return homeScore + " : " + awayScore;
    }
}
