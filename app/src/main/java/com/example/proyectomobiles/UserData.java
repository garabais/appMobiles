package com.example.proyectomobiles;

public class UserData {
    private String name, uid;

    public UserData(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }
}
