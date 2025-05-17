package com.example.footzone.model;

public class PredPlayer {
    private int id;
    private String name;
    private String imageUrl;
    private String position;

    public PredPlayer(int id, String name, String imageUrl, String position) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.position = position;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public String getPosition() { return position; }
}