package com.example.nitro;

import android.graphics.Color;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserInformation {

    public static String userId;
    public static String fullName;
    public static String photoUrl;
    public static FirebaseAuth auth;
    public static int themeId;
    public static int currentLevel;
    public static float averageTime;

    public static int leaderboardValue;

    public static int colorPlayer = Color.rgb(255, 0, 0);
    public static int colorWalls = Color.rgb(255, 255, 255);

    public static void reset(){
        userId = "none";
        fullName = "Guest";
        photoUrl = "@drawable/defaultuser";
        auth = null;
        themeId = 0;
        currentLevel = 0;
        averageTime = 1000;
        leaderboardValue = 0;
        colorPlayer = Color.rgb(255, 0, 0);
        colorWalls = Color.rgb(255, 255, 255);
    }
}
