package com.example.footzone.model;

import java.util.List;

public class Lineup {
    private String teamName;
    private String teamLogo;
    private String formation;
    private List<Player> players;

    public Lineup(String teamName, String teamLogo, String formation, List<Player> players) {
        this.teamName = teamName;

        this.formation = formation;
        this.players = players;
    }

    public String getTeamName() { return teamName; }
    public String getTeamLogo() { return teamLogo; }
    public String getFormation() { return formation; }
    public List<Player> getPlayers() { return players; }
}