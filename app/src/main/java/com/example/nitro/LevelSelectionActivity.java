package com.example.nitro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class LevelSelectionActivity extends AppCompatActivity {

    private LinearLayout linearParent;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_levelselection);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        linearParent = findViewById(R.id.linLayout);
        Query query = reference.child("mazes").orderByChild("id");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot mazes : dataSnapshot.getChildren()) {
                        // do something
                        //LinearLayout lin = new LinearLayout(LevelSelectionActivity.this);
                        //lin.setOrientation(LinearLayout.VERTICAL);

                        MazeData data;
                        try {
                            data = mazes.getValue(MazeData.class);
                        } catch (Exception e) {
                            continue;
                        }
                        Button levelBtn = new Button(LevelSelectionActivity.this);
                        //Log.d("levelData", String.valueOf(data.id));
                        levelBtn.setText("Maze " + String.valueOf(data.id));
                        levelBtn.setBackgroundColor(getResources().getColor(R.color.purple_200, getTheme()));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        params.setMargins(10,10,10,10);
                        levelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LevelInfo.id = data.id;
                                LevelInfo.maxTime = data.maxTime;
                                LevelInfo.mazeUrl = data.xmlUrl;
                                LevelInfo.sizeX = data.sizeX;
                                LevelInfo.sizeY = data.sizeY;
                                Intent i = new Intent(LevelSelectionActivity.this, MainActivity.class);
                                startActivity(i);
                            }
                        });
                        //lin.addView(levelBtn);
                        linearParent.addView(levelBtn, params);


                        Log.d("query", "maze data");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("query", "mazes not cool");
            }
        });
        Random rand = new Random();
        if(rand.nextInt(100) < 10){
            Log.d("TAG", "inside ad!");
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAd = interstitialAd;
                            mInterstitialAd.show(LevelSelectionActivity.this);

                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            mInterstitialAd = null;
                        }
                    });
//            if (mInterstitialAd != null) {
//                mInterstitialAd.show(LevelSelectionActivity.this);
//            } else {
//                Log.d("TAG", "The interstitial ad wasn't ready yet.");
//            }
        }


    }

    public void toGame(View v){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}