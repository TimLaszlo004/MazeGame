package com.example.nitro;

import android.graphics.Canvas;

import android.graphics.Rect;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ObstacleManager {
    private ArrayList<Obstacle> obstacles;
    private ArrayList<Obstacle> collidableObstacles;

    private int sizeX;
    private int sizeY;


    private int colorWall;

    private RectVector boundingBox;
    private List<RectVector> mazeLines;
    private String url;


    public ObstacleManager(int sizeX, int sizeY, int colorWall, String url){
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.url  = url;
        this.colorWall = colorWall;
        obstacles = new ArrayList<Obstacle>();
        mazeLines = new ArrayList<RectVector>();
        collidableObstacles = new ArrayList<Obstacle>();
        populateObstacles();
    }

    private void populateObstacles() {
        obstacles.clear();
        getMazeFile(); // async
        while(true){
            if(mazeLines.size() > 4){
                break;
            }
        }

        boundingBox = new RectVector(mazeLines.get(0).left-1, mazeLines.get(0).top-1, mazeLines.get(0).right+1, mazeLines.get(0).bottom+1);
        for(int i = 1; i < mazeLines.size(); i++){
            if(mazeLines.get(i).left < boundingBox.left){
                boundingBox.left = mazeLines.get(i).left;
            }
            if(mazeLines.get(i).top < boundingBox.top){
                boundingBox.top = mazeLines.get(i).top;
            }
            if(mazeLines.get(i).right > boundingBox.right){
                boundingBox.right = mazeLines.get(i).right;
            }
            if(mazeLines.get(i).bottom > boundingBox.bottom){
                boundingBox.bottom = mazeLines.get(i).bottom;
            }
        }


        // leaving 2-2 line of space from the edge of the screen
        // int obSize = (int)(Constants.SCREEN_WIDTH / (boundingBox.getWidth()+4));
        float sizeMultiplier = (Constants.SCREEN_WIDTH*0.9f)/(boundingBox.getWidth()*1.0f);
        int startX = Constants.SCREEN_WIDTH/2 - (int)(boundingBox.getWidth()*sizeMultiplier/2);
        int startY = Constants.SCREEN_HEIGHT/2 - (int)(boundingBox.getWidth()*sizeMultiplier/2);
        int wallWidth = 2;

        for(int i = 0; i < mazeLines.size(); i++){
            Rect r;
            if(mazeLines.get(i).top == mazeLines.get(i).bottom){
                // horizontal
                r = new Rect(startX+(int)(sizeMultiplier*(mazeLines.get(i).left)), startY+(int)(sizeMultiplier*(mazeLines.get(i).top)) -wallWidth,
                        startX+(int)(sizeMultiplier*(mazeLines.get(i).right)), startY+(int)(sizeMultiplier*(mazeLines.get(i).bottom)) +wallWidth);
                mazeLines.get(i).left = r.left;
                mazeLines.get(i).top = r.top+wallWidth;
                mazeLines.get(i).right = r.right;
                mazeLines.get(i).bottom = r.bottom-wallWidth;
            }
            else{
                r = new Rect(startX+(int)(sizeMultiplier*(mazeLines.get(i).left)) -wallWidth, startY+(int)(sizeMultiplier*(mazeLines.get(i).top)),
                        startX+(int)(sizeMultiplier*(mazeLines.get(i).right)) +wallWidth, startY+(int)(sizeMultiplier*(mazeLines.get(i).bottom)));
                mazeLines.get(i).left = r.left+wallWidth;
                mazeLines.get(i).top = r.top;
                mazeLines.get(i).right = r.right-wallWidth;
                mazeLines.get(i).bottom = r.bottom;
            }
            Obstacle o = new Obstacle(r, colorWall, 1, mazeLines.get(i));
            obstacles.add(o);
        }
        boundingBox.left = startX-wallWidth;
        boundingBox.top = startY-wallWidth;
        boundingBox.right = startX + (int)(boundingBox.right * sizeMultiplier)+wallWidth;
        boundingBox.bottom = startY + (int)(boundingBox.bottom * sizeMultiplier)+wallWidth;
    }

    public int getSize(){
        return (int)(Constants.SCREEN_WIDTH / (sizeX+4));
    }

    private void getMazeFile(){
        new Thread(new Runnable()
        {
            public void run()
            {
                mazeLines = getTextFromWeb(url); // URL
            }
        }).start();
    }

    public boolean isColliding(RectPlayer player, int x, int y, int xMem, int yMem, int width){
        for(int i = 0; i < obstacles.size(); i++){
            if(obstacles.get(i).playerCollide(player, x, y) || obstacles.get(i).pathCollideAll(xMem, yMem, x, y, width)){
                return true;
            }
        }
        return false;
    }

    public List<RectVector> getTextFromWeb(String urlString)
    {
        URLConnection feedUrl;
        List<RectVector> lines = new ArrayList<>();

        try
        {
            feedUrl = new URL(urlString).openConnection();
            InputStream is = feedUrl.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = null;

            while ((line = reader.readLine()) != null) // read line by line
            {
                String[] parts = line.split("\"");
                lines.add(new RectVector(Integer.valueOf(parts[1]), Integer.valueOf(parts[3]), Integer.valueOf(parts[5]), Integer.valueOf(parts[7])));
            }
            is.close(); // close input stream

            return lines; // return whatever you need
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public void draw(Canvas canvas){
        for(Obstacle obstacle : obstacles){
            obstacle.draw(canvas);
        }
    }

    public int getStartX(){
        return (int)(Constants.SCREEN_WIDTH/2);
    }
    public int getStartY(){
        return (int)(Constants.SCREEN_HEIGHT/2 + boundingBox.getHeight()/2) + 2*getSize();
    }
    public RectVector getEndRect(){
        RectVector v = new RectVector(
                (int)(boundingBox.left),
                (int)(boundingBox.top-3*getSize()),
                (int)(boundingBox.right),
                (int)(boundingBox.top)
        );
        return v;
    }

    public RectVector getBoundingBox(){
        RectVector v = new RectVector(
                (boundingBox.left),
                (boundingBox.top - 4*getSize()),
                (boundingBox.right),
                (boundingBox.bottom + 4*getSize())
        );
        return v;
    }
}
