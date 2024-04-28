package com.example.weightapp;

public class Weight {
    private int id;
    private int userId;
    private String weight;
    private String date;

    public Weight(int id, int userId, String weight, String date) {
        this.id = id;
        this.userId = userId;
        this.weight = weight;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getWeight() {
        return weight;
    }

    public String getDate() {
        return date;
    }
}
