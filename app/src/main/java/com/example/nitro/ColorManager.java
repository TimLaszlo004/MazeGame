package com.example.nitro;

import android.graphics.Color;

import java.util.ArrayList;

public class ColorManager {
    public static ArrayList<ColorScheme> colors;

    public ColorManager(){
        colors = new ArrayList<ColorScheme>();
        colors.add(new ColorScheme(Color.rgb(0,255,0), Color.rgb(255,0,0))); // default (greenred)
        colors.add(new ColorScheme(Color.rgb(255,0,0), Color.rgb(255,255,255))); // #1 (redwhite)
        colors.add(new ColorScheme(Color.rgb(255,100,35), Color.rgb(120,150,255))); // #2 (orangeblue) spec
        colors.add(new ColorScheme(Color.rgb(167,100,255), Color.rgb(120,255,0))); // #3 (purplegreen) spec
        colors.add(new ColorScheme(Color.rgb(255,255,255), Color.rgb(0,0,255))); // #4 (whiteblue)
        colors.add(new ColorScheme(Color.rgb(0,0,255), Color.rgb(255,0,255))); // #5 (bluepink)

        set(UserInformation.themeId);

    }

    public static void set(int id){
        UserInformation.colorWalls = colors.get(id).wall;
        UserInformation.colorPlayer = colors.get(id).player;

    }
}
