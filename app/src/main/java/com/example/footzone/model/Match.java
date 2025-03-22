package com.example.footzone.model;
public class Match {
    private String date;
    private String homeTeam;
    private String awayTeam;
    private String homeGoals;
    private String awayGoals;

    public Match(String date, String homeTeam, String awayTeam, String homeGoals, String awayGoals) {
        this.date = date;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
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

    public String getHomeGoals() {
        return homeGoals;
    }

    public String getAwayGoals() {
        return awayGoals;
    }
}
