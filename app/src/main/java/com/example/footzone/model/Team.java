package com.example.footzone.model;

public class Team {
    private String name;
    private String logoUrl;
    private boolean selected;

    public Team(String name, String logoUrl) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.selected = false;
    }

    public String getName() {
        return name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "Team{name='" + name + "', selected=" + selected + "}";
    }
}