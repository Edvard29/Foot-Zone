package com.example.footzone.model;

public class Transfer {
    private String playerName;
    private String playerTeam;
    private String playerPosition;
    private String transferDate;
    private String fromTeam;
    private String toTeam;
    private String transferFee;

    // Конструктор, который принимает 7 параметров
    public Transfer(String playerName, String playerTeam, String playerPosition, String transferDate,
                    String fromTeam, String toTeam, String transferFee) {
        this.playerName = playerName;
        this.playerTeam = playerTeam;
        this.playerPosition = playerPosition;
        this.transferDate = transferDate;
        this.fromTeam = fromTeam;
        this.toTeam = toTeam;
        this.transferFee = transferFee;
    }

    public Transfer(String playerName, String fromTeam, String toTeam, String transferDate) {
    }

    // Getter'ы и Setter'ы
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerTeam() {
        return playerTeam;
    }

    public void setPlayerTeam(String playerTeam) {
        this.playerTeam = playerTeam;
    }

    public String getPlayerPosition() {
        return playerPosition;
    }

    public void setPlayerPosition(String playerPosition) {
        this.playerPosition = playerPosition;
    }

    public String getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(String transferDate) {
        this.transferDate = transferDate;
    }

    public String getFromTeam() {
        return fromTeam;
    }

    public void setFromTeam(String fromTeam) {
        this.fromTeam = fromTeam;
    }

    public String getToTeam() {
        return toTeam;
    }

    public void setToTeam(String toTeam) {
        this.toTeam = toTeam;
    }

    public String getTransferFee() {
        return transferFee;
    }

    public void setTransferFee(String transferFee) {
        this.transferFee = transferFee;
    }
}
