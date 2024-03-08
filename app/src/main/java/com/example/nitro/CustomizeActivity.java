package com.example.nitro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class CustomizeActivity extends AppCompatActivity {


    private TextView currentTxt;
    private ImageView currentTheme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_customize);
        currentTxt = (TextView) findViewById(R.id.current);
        currentTheme = (ImageView) findViewById(R.id.themeImg);

        if(UserInformation.themeId == 0){
            currentTxt.setText("Default");
        }
        else{
            currentTxt.setText("#" + String.valueOf(UserInformation.themeId));
        }
        Log.d("theme", String.valueOf(UserInformation.themeId));

        switch (UserInformation.themeId){
            case 0:
                Picasso.get().load(R.drawable.greenred).resize(400, 400).into(currentTheme);
                break;
            case 1:
                Picasso.get().load(R.drawable.redwhite).resize(400, 400).into(currentTheme);
                break;
            case 2:
                Picasso.get().load(R.drawable.orangeblue).resize(400, 400).into(currentTheme);
                break;
            case 3:
                Picasso.get().load(R.drawable.purplegreen).resize(400, 400).into(currentTheme);
                break;
            case 4:
                Picasso.get().load(R.drawable.whiteblue).resize(400, 400).into(currentTheme);
                break;
            case 5:
                Picasso.get().load(R.drawable.bluepink).resize(400, 400).into(currentTheme);
                break;
        }
    }

    public void setDef(View v){
        UserInformation.themeId = 0;
        ColorManager.set(0);
        currentTxt.setText("Default");
        Picasso.get().load(R.drawable.greenred).resize(400, 400).into(currentTheme);
        saveToDatabase();
    }

    public void setOne(View v){
        UserInformation.themeId = 1;
        ColorManager.set(1);
        currentTxt.setText("#1");
        Picasso.get().load(R.drawable.redwhite).resize(400, 400).into(currentTheme);
        saveToDatabase();
    }

    public void setTwo(View v){
        UserInformation.themeId = 2;
        ColorManager.set(2);
        currentTxt.setText("#2");
        Picasso.get().load(R.drawable.orangeblue).resize(400, 400).into(currentTheme);
        saveToDatabase();
    }

    public void setThree(View v){
        UserInformation.themeId = 3;
        ColorManager.set(3);
        currentTxt.setText("#3");
        Picasso.get().load(R.drawable.purplegreen).resize(400, 400).into(currentTheme);
        saveToDatabase();
    }

    public void setFour(View v){
        UserInformation.themeId = 4;
        ColorManager.set(4);
        currentTxt.setText("#4");
        Picasso.get().load(R.drawable.whiteblue).resize(400, 400).into(currentTheme);
        saveToDatabase();
    }

    public void setFive(View v){
        UserInformation.themeId = 5;
        ColorManager.set(5);
        currentTxt.setText("#5");
        Picasso.get().load(R.drawable.bluepink).resize(400, 400).into(currentTheme);
        saveToDatabase();
    }

    public void saveToDatabase(){
        if(UserInformation.userId == "none"){return;}
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(UserInformation.userId).child("themeId").setValue(UserInformation.themeId);
    }
}