package com.example.nitro;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    Button googleAuthBtn;
    // FirebaseAuth auth;

    FirebaseDatabase database;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign);

        //sign out
        if(UserInformation.auth != null){
            UserInformation.auth.signOut();
            if(UserInformation.userId != "none"){
                Toast.makeText(SignInActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
            }
        }
        UserInformation.reset();

        //auth setup
        googleAuthBtn = findViewById(R.id.btnAuth);
        UserInformation.auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1064019779857-4oflalcqbl1uab2ohfk757lf6d8slotp.apps.googleusercontent.com")
                .requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleAuthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });
        if(database == null){
            Log.d("database", "Something is really fucked up...");
        }
    }

    private void googleSignIn(){
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            }
            catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        UserInformation.auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if  (task.isSuccessful()){
                            FirebaseUser user = UserInformation.auth.getCurrentUser();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id", user.getUid());
                            map.put("name", user.getDisplayName());
                            map.put("profile", user.getPhotoUrl().toString());
                            map.put("leaderboardValue", 0);
                            map.put("themeId", 0);
                            boolean isNew = false;
                            if(database.getReference().child("users").child(user.getUid()) == null){
                                database.getReference().child("users").child(user.getUid()).setValue(map);
                                UserInformation.leaderboardValue = 0;
                                isNew = true;
                            }
                            UserInformation.userId = user.getUid();
                            UserInformation.fullName = user.getDisplayName();
                            UserInformation.photoUrl = user.getPhotoUrl().toString();
                            if(!isNew){
                                Log.d("userValue", "trying get values");
                                Query query = database.getReference().child("users").child(UserInformation.userId);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            UserData data;
                                            try{
                                                data = dataSnapshot.getValue(UserData.class);
                                                UserInformation.leaderboardValue = data.leaderboardValue;
                                                UserInformation.themeId = data.themeId;
                                            }
                                            catch(Exception e){
                                                Log.d("userValue", "data.toString()");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }



                            Intent intent = new Intent(SignInActivity.this, Game.class);
                            startActivity(intent);

                        }
                        else{
                            Toast.makeText(SignInActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void asGuest(View view){
        UserInformation.userId = "none";
        UserInformation.fullName = "Guest";
        UserInformation.photoUrl = "https://github.com/TimLaszlo004/szoftech-elm-let/blob/main/default.jpg";
        Intent i = new Intent(this, Game.class);
        //finish();
        startActivity(i);
    }
}