package com.example.nitro;





import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private MainThread thread;
    private RectPlayer player;
    private Point playerPoint;

    private ObstacleManager obManager;

    private final Drawable backGround;
    private final Drawable pauseBtn;
    private final Drawable resumeBtn;
    private final Drawable nextBtn;
    private final Drawable backBtn;
    private final Drawable restartBtn;
    private final Drawable winPanel;
    private float timer;
    private final MainActivity game;

    private boolean isPaused = false;

    private final float collideBounceBackMultiplier = 0.003f;
    private int state;
    private RectVector end;
    private Rect boundingRect;



    public GamePanel(Context context, MainActivity game){
        super(context);
        state = 0;
        this.game = game;
        timer = LevelInfo.maxTime;
        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);
        Log.d("list", LevelInfo.mazeUrl);
        obManager = new ObstacleManager(LevelInfo.sizeX, LevelInfo.sizeY, UserInformation.colorWalls, LevelInfo.mazeUrl);
        player = new RectPlayer(new Rect(100, 100, 100+((int)(obManager.getSize()*0.45)), 100+((int)(obManager.getSize()*0.45))), UserInformation.colorPlayer);
        playerPoint = new Point(obManager.getStartX(), obManager.getStartY());
        RectVector b = obManager.getBoundingBox();
        boundingRect = new Rect(b.left, b.top, b.right, b.bottom);
        end = obManager.getEndRect();

        // UI
        backGround = getResources().getDrawable(R.drawable.background2, null);
        pauseBtn = getResources().getDrawable(R.drawable.gamepause, null);
        resumeBtn = getResources().getDrawable(R.drawable.gameplay, null);
        nextBtn = getResources().getDrawable(R.drawable.nextbtnicon, null);
        backBtn = getResources().getDrawable(R.drawable.backbtnicon, null);
        restartBtn = getResources().getDrawable(R.drawable.restartbtnicon, null);
        winPanel = getResources().getDrawable(R.drawable.winpanel, null);

        int buttonSize = 70;
        int panelSize = (int)(Constants.SCREEN_WIDTH*0.5f);
        int pauseHeight = Constants.SCREEN_HEIGHT-2*buttonSize - 50;
        backGround.setBounds(0,0,Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);
        pauseBtn.setBounds((int)(Constants.SCREEN_WIDTH/2-buttonSize), pauseHeight-2*buttonSize, (int)(Constants.SCREEN_WIDTH/2+buttonSize), pauseHeight);
        resumeBtn.setBounds((int)(Constants.SCREEN_WIDTH/2-buttonSize), pauseHeight-2*buttonSize, (int)(Constants.SCREEN_WIDTH/2+buttonSize), pauseHeight);
        nextBtn.setBounds((int)(3*Constants.SCREEN_WIDTH/4-buttonSize), pauseHeight-2*buttonSize, (int)(3*Constants.SCREEN_WIDTH/4+buttonSize), pauseHeight);
        backBtn.setBounds(24, 24, 2*buttonSize+24, 2*buttonSize+24);
        restartBtn.setBounds((int)(Constants.SCREEN_WIDTH/4-buttonSize), pauseHeight-2*buttonSize, (int)(Constants.SCREEN_WIDTH/4+buttonSize), pauseHeight);
        winPanel.setBounds((int)(Constants.SCREEN_WIDTH/2-panelSize), (int)(Constants.SCREEN_HEIGHT/2-panelSize), (int)(Constants.SCREEN_WIDTH/2+panelSize), (int)(Constants.SCREEN_HEIGHT/2+panelSize));



        setFocusable(true);
    }

    private void restart(){
        state = 0;
        timer = LevelInfo.maxTime;
        playerPoint.set(obManager.getStartX(), obManager.getStartY());
        player.update(playerPoint);
    }

    private void nextMaze(){
        int nextId = LevelInfo.id+1;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("mazes").orderByChild("id").equalTo(nextId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot mazes : dataSnapshot.getChildren()) {
                        // do something


                        MazeData data;
                        try {
                            data = mazes.getValue(MazeData.class);
                        } catch (Exception e) {
                            continue;
                        }
                        LevelInfo.id = data.id;
                        LevelInfo.mazeUrl = data.xmlUrl;
                        LevelInfo.sizeX = data.sizeX;
                        LevelInfo.sizeY = data.sizeY;
                        LevelInfo.maxTime = data.maxTime;

                        obManager = new ObstacleManager(LevelInfo.sizeX, LevelInfo.sizeY, UserInformation.colorWalls, LevelInfo.mazeUrl);
                        player = new RectPlayer(new Rect(100, 100, 100+((int)(obManager.getSize()*0.45)), 100+((int)(obManager.getSize()*0.45))), UserInformation.colorPlayer);
                        playerPoint = new Point(obManager.getStartX(), obManager.getStartY());
                        RectVector b = obManager.getBoundingBox();
                        boundingRect = new Rect(b.left, b.top, b.right, b.bottom);
                        end = obManager.getEndRect();
                        restart();

                        break;
                    }
                }
                else{
                    game.toLevels();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("query", "mazes not cool");
                game.toLevels();
            }
        });
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        thread = new MainThread(getHolder(), this);

        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        while(retry){
            try{
                thread.setRunning(false);
                thread.join();
            } catch(Exception e){e.printStackTrace();}
            retry = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(pauseBtn.getBounds().top < (int)event.getY() && pauseBtn.getBounds().bottom > (int)event.getY()
        && pauseBtn.getBounds().right > (int)event.getX() && pauseBtn.getBounds().left < (int)event.getX()){
            isPaused = !isPaused;
        }
        else if(nextBtn.getBounds().top < (int)event.getY() && nextBtn.getBounds().bottom > (int)event.getY()
                && nextBtn.getBounds().right > (int)event.getX() && nextBtn.getBounds().left < (int)event.getX()){
            Log.d("btn", "next level");
            nextMaze();
        }
        else if(backBtn.getBounds().top < (int)event.getY() && backBtn.getBounds().bottom > (int)event.getY()
                && backBtn.getBounds().right > (int)event.getX() && backBtn.getBounds().left < (int)event.getX()){
            Log.d("btn", "back");
            game.toLevels();
        }
        else if(restartBtn.getBounds().top < (int)event.getY() && restartBtn.getBounds().bottom > (int)event.getY()
                && restartBtn.getBounds().right > (int)event.getX() && restartBtn.getBounds().left < (int)event.getX()){
            Log.d("btn", "restart");
            restart();
        }

        return super.onTouchEvent(event);
    }

    public void update() {
        if (isPaused) {
            return;
        }
        timer -= Constants.deltaTime*0.000000001;
        if(timer <= 0){
            restart();
        }
        if(!boundingRect.contains(playerPoint.x, playerPoint.y)){
            restart();
        }
        if(end.contains(playerPoint.x, playerPoint.y)){
            state = 1;

            isPaused = true;
            // send data
            if(UserInformation.userId != "none"){
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("users").child(UserInformation.userId).child("leaderboardValue").setValue(UserInformation.leaderboardValue+1);
                UserInformation.leaderboardValue++;
            }
            else{
                Toast.makeText(game, "Sign in to save data", Toast.LENGTH_SHORT).show();
            }

        }
        // cancel motion
        int pointMemX = playerPoint.x;
        int pointMemY = playerPoint.y;
        playerPoint.set((int) (playerPoint.x - MainActivity.sensorInput[0] * 0.00000005 * Constants.deltaTime), (int) (playerPoint.y + MainActivity.sensorInput[1] * 0.00000005 * Constants.deltaTime));
        if (playerPoint.x < 0 || playerPoint.x > Constants.SCREEN_WIDTH || playerPoint.y < 0 || playerPoint.y > Constants.SCREEN_HEIGHT) {
            playerPoint.set((int) (Constants.SCREEN_WIDTH / 2), (int) (Constants.SCREEN_HEIGHT / 2));
        }
        player.update(playerPoint);
        int newPlayerPointX = playerPoint.x;
        int newPlayerPointY = playerPoint.y;
        if(obManager.isColliding(player, playerPoint.x, playerPoint.y, pointMemX, pointMemY, player.getRect().width()/2)){
//            if(Abs(playerPoint.x - pointMemX) + Abs(playerPoint.y-pointMemY) <= collideBounceBackThreshold){
//                playerPoint.set(pointMemX + (int)(collideBounceBackMultiplier*(pointMemX-playerPoint.x)), pointMemY + (int)(collideBounceBackMultiplier*(pointMemY-playerPoint.y)));
//                Log.d("bounce", "bounced");
//            }
            if(true){
                boolean isXCollision = false;
                boolean isYCollision = false;
                if((newPlayerPointX - pointMemX) > 0){
                    playerPoint.set(pointMemX+3, pointMemY);
                    player.update(playerPoint);
                    if(obManager.isColliding(player, playerPoint.x, playerPoint.y, pointMemX, pointMemY, player.getRect().width()/2)){
                        isXCollision = true;
                    }
                }
                else if ((newPlayerPointX - pointMemX) < 0){
                    playerPoint.set(pointMemX-3, pointMemY);
                    player.update(playerPoint);
                    if(obManager.isColliding(player, playerPoint.x, playerPoint.y, pointMemX, pointMemY, player.getRect().width()/2)){
                        isXCollision = true;
                    }
                }
                if((newPlayerPointY - pointMemY) > 0){
                    playerPoint.set(pointMemX, pointMemY+3);
                    player.update(playerPoint);
                    if(obManager.isColliding(player, playerPoint.x, playerPoint.y, pointMemX, pointMemY, player.getRect().width()/2)){
                        isYCollision = true;
                    }
                }
                else if ((newPlayerPointY - pointMemY) < 0){
                    playerPoint.set(pointMemX, pointMemY-3);
                    player.update(playerPoint);
                    if(obManager.isColliding(player, playerPoint.x, playerPoint.y, pointMemX, pointMemY, player.getRect().width()/2)){
                        isYCollision = true;
                    }
                }
                if(isYCollision){
                    Log.d("coll", "collcool");
                    if(isXCollision){
                        playerPoint.set(pointMemX + (int)(collideBounceBackMultiplier*(pointMemX-newPlayerPointX)), pointMemY + (int)(collideBounceBackMultiplier*(pointMemY-newPlayerPointY)));
                    }
                    else{
                        playerPoint.set(newPlayerPointX, pointMemY + (int)(collideBounceBackMultiplier*(pointMemY-newPlayerPointY)));
                    }
                }
                else if(isXCollision){
                    Log.d("coll", "collcool");
                    playerPoint.set(pointMemX + (int)(collideBounceBackMultiplier*(pointMemX-newPlayerPointX)), newPlayerPointY);
                }
                else{
                    Log.d("coll", "WTF???");
                }
            }
//            else{
//                playerPoint.set(pointMemX, pointMemY);
//            }
            player.update(playerPoint);
        }
    }

    private int Abs(int number){
        if(number < 0){
            return -number;
        }
        return number;
    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);
        canvas.drawColor(Color.rgb(0,0,0));
        // backGround.draw(canvas);
        // Paint p = new Paint();
        // p.setColor(Color.rgb(20, 20, 20));
        // canvas.drawRect(boundingRect, p);

        // draw maze
        obManager.draw(canvas);

        // draw player
        player.draw(canvas);

        // draw ui
        restartBtn.draw(canvas);
        if(state == 0){
            if(isPaused){
                resumeBtn.draw(canvas);
                backBtn.draw(canvas);
            }
            else{
                pauseBtn.draw(canvas);
            }
        }
        else{
            backBtn.draw(canvas);
            nextBtn.draw(canvas);
            winPanel.draw(canvas);
            Paint timePaint = new Paint();
            timePaint.setStyle(Paint.Style.FILL);

            timePaint.setColor(Color.WHITE);
            timePaint.setTextSize(80);
            canvas.drawText(format2Digits((LevelInfo.maxTime- timer)/60) + " : " + format2Digits((LevelInfo.maxTime- timer)%60 +1), (int)(Constants.SCREEN_WIDTH/2 - 110), (int)(Constants.SCREEN_HEIGHT/2 + 200), timePaint);
            Log.d("panel", "win panel");

        }
        // timer text
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(Color.WHITE);
        paint.setTextSize(80);
        canvas.drawText("Maze "+ LevelInfo.id, (int)(Constants.SCREEN_WIDTH/2 - 95), 120, paint);
        canvas.drawText( format2Digits(timer/60) + " : " + format2Digits(timer%60), (int)(Constants.SCREEN_WIDTH/2 - 85), 200, paint);
    }

    private String format2Digits(double number){
        int n = (int)number;
        if(n < 10){
            return "0" + String.valueOf(n);
        }
        else{
            return String.valueOf(n);
        }
    }
}
