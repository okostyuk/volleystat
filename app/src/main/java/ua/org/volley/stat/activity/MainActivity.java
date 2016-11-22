package ua.org.volley.stat.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import ua.org.volley.stat.DBWrapper;
import ua.org.volley.stat.R;
import ua.org.volley.stat.model.Player;
import ua.org.volley.stat.model.Team;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "mainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBWrapper.init(this);

        findViewById(R.id.createTestData).setOnClickListener(this);
        findViewById(R.id.newGameButtom).setOnClickListener(this);

        findViewById(R.id.newGameButtom).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startActivity(new Intent(MainActivity.this, SelectTeamsActivity.class));
                return true;
            }
        });

        final View progressBar = findViewById(R.id.progressBar);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "onAuthStateChanged: " + firebaseAuth.getCurrentUser());
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInAnonymously();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.createTestData:
                createTestData();
                break;
            case R.id.newGameButtom:
                startActivity(new Intent(this, GameActivity.class));
                break;
        }
    }

    private void createTestData() {

        Team olimp = DBWrapper.createTeam("Olimp");
        Team mib = DBWrapper.createTeam("MIB");

        for (int i=1; i<11; i++){
            Player player = DBWrapper.createPlayer("Player " + i);
            DBWrapper.createTeamPlayer(olimp, player, i);
        }

        for (int i = 11; i<21; i++){
            Player player = DBWrapper.createPlayer("Player " + i);
            DBWrapper.createTeamPlayer(mib, player, i);
        }

    }


}
