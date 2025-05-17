package com.example.footzone.model;

public class Footballer {
    private String name;
    private int goals;
    private int assists;
    private String imageUrl;

    public Footballer(String name, int goals, int assists, String imageUrl) {
        this.name = name;
        this.goals = goals;
        this.assists = assists;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public int getGoals() {
        return goals;
    }

    public int getAssists() {
        return assists;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}