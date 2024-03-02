package com.example.nitro;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread{
    public static final int MAX_FPS = 60;
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;
    public static Canvas canvas;

    public void setRunning(boolean running){
        this.running = running;
    }
    public MainThread(SurfaceHolder holder, GamePanel panel){
        super();
        this.surfaceHolder = holder;
        this.gamePanel = panel;
    }

    @Override
    public void run(){
        long startTime;
        long timeMillis = 1000/MAX_FPS;
        long waitTime;
        int frameCount = 0;
        long totalTime = 0;
        long targetTime = 1000/MAX_FPS;

        while(running){
            startTime = System.nanoTime();
            canvas = null;

            try{
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){
                    this.gamePanel.update();
                    this.gamePanel.draw(canvas);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
            finally{
                if(canvas != null){
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            timeMillis = (System.nanoTime()-startTime)/1000000;
            waitTime = targetTime - timeMillis;
            try{
                if(waitTime > 0){
                    this.sleep(waitTime);
                }
            } catch (Exception e){e.printStackTrace();}
            Constants.deltaTime = System.nanoTime() - startTime;
            totalTime += Constants.deltaTime;
            frameCount++;
            if(frameCount == MAX_FPS){
                averageFPS = 1000 / ((totalTime/frameCount)/1000000);
                frameCount = 0;
                totalTime = 0;
            }
        }
    }
}
