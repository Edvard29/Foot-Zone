package com.example.footzone.model;

public class TeamStanding {
    private String name;
    private boolean isHeader;
    private int points;
    private String logoUrl; // URL логотипа команды
    private int played; // Сыгранные матчи
    private int wins; // Победы
    private int losses; // Поражения

    // Для заголовков лиг
    public TeamStanding(String name, boolean isHeader) {
        this.name = name;
        this.isHeader = isHeader;
        this.points = 0;
        this.played = 0;
        this.wins = 0;
        this.losses = 0;
        this.logoUrl = null;
    }

    // Для команд
    public TeamStanding(String name, int points, String logoUrl, int played, int wins, int losses) {
        this.name = name;
        this.isHeader = false;
        this.points = points;
        this.logoUrl = logoUrl;
        this.played = played;
        this.wins = wins;
        this.losses = losses;
    }

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(int played) {
        this.played = played;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }
}