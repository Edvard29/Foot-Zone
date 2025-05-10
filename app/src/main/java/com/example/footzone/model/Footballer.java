package com.example.footzone.model;

public class Footballer {
    private String name;
    private String team;
    private int goals;  // Количество голов
    private int assists;  // Количество ассистов

    public Footballer(String name, String team, int goals, int assists) {
        this.name = name;
        this.team = team;
        this.goals = goals;
        this.assists = assists;
    }

    // Конструктор для ассистов (если нужно)
    public Footballer(String name, String team, int assists) {
        this.name = name;
        this.team = team;
        this.goals = 0;  // Если ассисты, но нет голов
        this.assists = assists;
    }



    public String getName() {
        return name;
    }

    public String getTeam() {
        return team;
    }

    public int getGoals() {
        return goals;
    }

    public int getAssists() {
        return assists;
    }

    // Метод для отображения статистики
    public String getStat() {
        return "Голы: " + goals + " Ассисты: " + assists;
    }
}