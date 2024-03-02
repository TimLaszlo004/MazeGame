package com.example.nitro;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.squareup.picasso.Picasso;

public class Game extends AppCompatActivity {
// android:theme="@android:style/Theme.Holo.NoActionBar.TranslucentDecor"
    ImageView photo;
    TextView nameTxt;
    ColorManager colorManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_game);

        photo = findViewById(R.id.photoImg);
        nameTxt = findViewById(R.id.nameTxt);
        if(UserInformation.userId != "none"){
            Picasso.get().load(UserInformation.photoUrl).resize(400, 400).into(photo);
        }
        else{
            Picasso.get().load(R.drawable.defaultuser).resize(400, 400).into(photo);
        }
        nameTxt.setText(UserInformation.fullName);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        colorManager = new ColorManager();



    }

    public void ToLevels(View v){
        Intent i = new Intent(this, LevelSelectionActivity.class);
        // finish();
        startActivity(i);
    }

    public void ToLeaderboard(View v){
        Intent i = new Intent(this, Leaderboard.class);
        // finish();
        startActivity(i);
    }

    public void ToAchievements(View v){
        Intent i = new Intent(this, Achievements.class);
        // finish();
        startActivity(i);
    }

    public void ToCustomize(View v){
        Intent i = new Intent(this, CustomizeActivity.class);
        // finish();
        startActivity(i);
    }

    public void SignOut(View v){
        if(UserInformation.userId != "none"){
            UserInformation.auth.signOut();
        }
        Intent i = new Intent(this, SignInActivity.class);
        // finish();
        startActivity(i);
    }



}