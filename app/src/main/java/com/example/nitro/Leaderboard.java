package com.example.nitro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class Leaderboard extends AppCompatActivity {

    TextView yourData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_leaderboard);
        LinearLayout linearLayout = findViewById(R.id.scrolllinear);
        yourData = (TextView)findViewById(R.id.textView5);
        ArrayList<UserData> allUser = new ArrayList<UserData>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child("users").orderByChild("leaderboardValue");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // int i = 1;
                    for (DataSnapshot users : dataSnapshot.getChildren()) {
                        // do something
                        UserData data;
                        try{
                            data = users.getValue(UserData.class);
                            allUser.add(data);
                        }
                        catch(Exception e){
                            continue;
                        }
                    }
                    Collections.reverse(allUser);
                    for(int i = 0; i < allUser.size(); i++){
                        if(allUser.get(i).id.equals(UserInformation.userId)){
                            yourData.setText("You are number " + String.valueOf(i+1) + ". (Your result is: " +
                             String.valueOf(allUser.get(i).leaderboardValue) + ")");
                        }
                        TextView txtV = new TextView(Leaderboard.this);
                        txtV.setText(String.valueOf(i+1) + ". " + allUser.get(i).name + "    " + allUser.get(i).leaderboardValue);
                        txtV.setTextSize(25);
                        txtV.setBackgroundColor(getResources().getColor(R.color.purple_200, getTheme()));
                        LinearLayout.LayoutParams parms  = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        parms.setMargins(10, 10, 10, 10);
                        txtV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        linearLayout.addView(txtV, parms);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}