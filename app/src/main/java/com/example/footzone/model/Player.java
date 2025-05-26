package com.example.footzone.model;

public class Player {
    private String name;
    private String position;
    private int age;
    private String photoUrl;

    public Player(String name, String position, int age, String photoUrl) {
        this.name = name;
        this.position = position;
        this.age = age;
        this.photoUrl = photoUrl;
    }

    public String getName() { return name; }
    public String getPosition() { return position; }
    public int getAge() { return age; }
    public String getPhotoUrl() { return photoUrl; }
}