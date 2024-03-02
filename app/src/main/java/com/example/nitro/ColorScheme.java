package com.example.nitro;

public class ColorScheme {
    public int wall;
    public int player;

    public ColorScheme(int wallColor, int playerColor){
        this.wall = wallColor;
        this.player = playerColor;
    }

    public void set(){
        UserInformation.colorPlayer = player;
        UserInformation.colorWalls = wall;
    }

}
