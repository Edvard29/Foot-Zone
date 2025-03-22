package com.example.footzone.model;




public class TeamStanding {
    private String name;
    private boolean isHeader;
    private int points;

    public TeamStanding(String name, boolean isHeader) {
        this.name = name;
        this.points = points;
        this.isHeader = isHeader;
    }

    public TeamStanding(String name, int points) {
        this.name = name;
        this.points = points;
        this.isHeader = false;
    }

    public TeamStanding(String name, boolean isHeader, int points) {
        this.name = name;
        this.points = points;
        this.isHeader = isHeader;
    }


    public String getName() {
        return name;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public int getPoints() {
        return points;
    }

    public String getTeamName() {
        return name;
    }
}
