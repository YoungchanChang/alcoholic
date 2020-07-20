package com.techtown.alcoholic;

public class GameResultItem {
    String user;
    String score;


    public GameResultItem(String user, String score) {
        this.user = user;
        this.score = score;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
