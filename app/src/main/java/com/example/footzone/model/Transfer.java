package com.example.footzone.model;

public class Transfer {
    private String playerName;
    private String fromTeam;
    private String toTeam;
    private String date;
    private String fee;

    public Transfer(String playerName, String fromTeam, String toTeam, String date, String fee) {
        this.playerName = playerName;
        this.fromTeam = fromTeam;
        this.toTeam = toTeam;
        this.date = date;
        this.fee = fee;
    }

    public String getPlayerName() { return playerName; }
    public String getFromTeam() { return fromTeam; }
    public String getToTeam() { return toTeam; }
    public String getDate() { return date; }
    public String getFee() { return fee; }
}