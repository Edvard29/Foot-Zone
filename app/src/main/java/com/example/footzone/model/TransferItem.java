package com.example.footzone.model;

public class TransferItem {
    private String playerName;
    private String fromTeam;
    private String toTeam;

    public TransferItem(String playerName, String fromTeam, String toTeam) {
        this.playerName = playerName;
        this.fromTeam = fromTeam;
        this.toTeam = toTeam;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getFromTeam() {
        return fromTeam;
    }

    public String getToTeam() {
        return toTeam;
    }
}

