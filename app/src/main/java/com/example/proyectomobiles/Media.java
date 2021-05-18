package com.example.proyectomobiles;

public class Media {
    private int id;
    private String name, description, url, date;
    private Double score;

    public Media(int id, String name, String description, String url, String date, Double score) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.score = score;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public Double getScore() {
        return score;
    }
}
