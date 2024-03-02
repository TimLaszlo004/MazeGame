package com.example.nitro;

public class UserData {
    public String id;
    public int leaderboardValue;
    public String name;
    public String profile;
    public int themeId;

    @Override
    public String toString(){
        return id + ", " + String.valueOf(leaderboardValue)+  ", " + name + ", " + profile + ", " + String.valueOf(themeId);
    }
}
