package com.example.footzone.model;

public class TeamStanding {
    private String teamName;
    private boolean isLeagueName;
    private int points;

    // Конструктор с тремя параметрами (оставляем, если нужно для других случаев)
    public TeamStanding(String teamName, boolean isLeagueName, int points) {
        this.teamName = teamName;
        this.isLeagueName = isLeagueName;
        this.points = points;
    }

    // Конструктор с двумя параметрами (если булевый параметр не нужен)
    public TeamStanding(String teamName, int points) {
        this.teamName = teamName;
        this.isLeagueName = false;  // или любое другое значение по умолчанию
        this.points = points;
    }

    // Геттеры и сеттеры для всех полей
    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public boolean isLeagueName() {
        return isLeagueName;
    }

    public void setLeagueName(boolean leagueName) {
        isLeagueName = leagueName;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
