package com.example.nitro;


public class RectVector {
    public int left;
    public int top;
    public int right;
    public int bottom;

    public RectVector(int left, int top, int right, int bottom){
        this.top = top;
        this.left = left;
        this.right = right;
        this.bottom = bottom;
    }

    public int getWidth(){
        return right-left;
    }

    public int getHeight(){
        return bottom-top;
    }

    @Override
    public String toString(){
        return "RectVector: " + String.valueOf(left) + ", " + String.valueOf(top) + ", " + String.valueOf(right) + ", " + String.valueOf(bottom);
    }

    public boolean contains(int x, int y){
        if(x<right && x > left && y > top && y < bottom){
            return true;
        }
        return false;
    }
}
