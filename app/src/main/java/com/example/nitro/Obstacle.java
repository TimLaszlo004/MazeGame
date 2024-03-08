package com.example.nitro;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class Obstacle implements GameObject {
    private Rect rectangle;
    private int color;

    private int logicColor;
    private RectVector line;

    public Obstacle(Rect rectangle, int color, int logicColor, RectVector line) {
        this.rectangle = rectangle;
        this.color = color;
        this.logicColor = logicColor;
        this.line = line;
    }

    public Rect getRectangle(){
        return rectangle;
    }

    public boolean playerCollide(RectPlayer player, int x, int y){
        if(rectangle.contains(player.getRect().left, player.getRect().top)
            ||rectangle.contains(player.getRect().right, player.getRect().top)
            ||rectangle.contains(player.getRect().left, player.getRect().bottom)
            ||rectangle.contains(player.getRect().right, player.getRect().bottom)
            ||rectangle.contains(x, y)){
            return true;
        }
        if(rectangle.left > player.getRect().left && rectangle.right < player.getRect().right
            && (rectangle.top <= player.getRect().bottom && rectangle.bottom >= player.getRect().top)){
            return true;
        }
        if(line.top == line.bottom){
            if(player.getRect().top < line.top && player.getRect().bottom > line.top){
                if(player.getRect().right > line.left && player.getRect().left < line.left){
                    return true;
                }
                else if(player.getRect().right > line.right && player.getRect().left < line.right){
                    return true;
                }
            }
        }
        else{
            if(player.getRect().left < line.left && player.getRect().right > line.left){
                if(player.getRect().bottom > line.top && player.getRect().top < line.top){
                    return true;
                }
                else if(player.getRect().bottom > line.bottom && player.getRect().top < line.bottom){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean pathCollide(int x1, int y1, int x2, int y2){
        if(line.top == line.bottom){
            // horizontal
            if(line.top < y1 && line.top > y2 || line.top < y2 && line.top > y1){
                double interpolation = ((y1-line.top)*1.0)/(y1-y2);
                int interX = x1 + (int)((x2-x1)*interpolation);
                if(line.left <= interX && line.right >= interX){
                    return true;
                }
            }
        }
        else if (line.left == line.right){
            // vertical
            if(line.left < x1 && line.left > x2 || line.left < x2 && line.left > x1){
                double interpolation = ((x1-line.left)*1.0)/(x1-x2);
                int interY = y1 + (int)((y2-y1)*interpolation);
                if(line.top <= interY && line.bottom >= interY){
                    return true;
                }
            }
        }

        return false;
    }

    public boolean pathCollideAll(int x1, int y1, int x2, int y2, int width){
        if(pathCollide(x1, y1, x2, y2)
                || pathCollide(x1-width, y1-width, x2-width, y2-width)
                || pathCollide(x1-width, y1+width, x2-width, y2+width)
                || pathCollide(x1+width, y1-width, x2+width, y2-width)
                || pathCollide(x1+width, y1+width, x2+width, y2+width)){
            return true;
        }
        return false;
    }


    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(rectangle, paint);
    }


    public void update(Point point) {
        rectangle.set(point.x - rectangle.width()/2, point.y - rectangle.height()/2, point.x + rectangle.width()/2, point.y + rectangle.height()/2 );
    }

    @Override
    public void update(){

    }
}
