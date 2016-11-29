package ua.org.volley.stat.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import ua.org.volley.stat.DBWrapper;
import ua.org.volley.stat.R;
import ua.org.volley.stat.model.Player;
import ua.org.volley.stat.model.Team;
import ua.org.volley.stat.model.TeamPlayer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "mainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBWrapper.init(this);

        findViewById(R.id.createTestData).setOnClickListener(this);
        findViewById(R.id.newGameButtom).setOnClickListener(this);

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
                startActivity(new Intent(this, SelectTeamsActivity.class));
                break;
        }
    }

    private void createTestData() {
        try {
            List<TeamPlayer> teamPlayerList = readFrom("players");
            Map<String, Team> teams = new HashMap<>();
            for (TeamPlayer player : teamPlayerList){
                Team team = teams.get(player.teamName);
                if (team == null){
                    team = DBWrapper.addTeam(player.teamName, player.teamName);
                    teams.put(team.name, team);
                }
                player.teamId = team.id;
                team.addPlayer(player);
                addPlayer(player, team);
                DBWrapper.updateFirebaseRecord(team, DBWrapper.TEAMS);
            }

        } catch (IOException e) {
            Log.e(TAG, "createTestData: ", e);
        }
    }

    private Player addPlayer(TeamPlayer teamPlayer, Team team) {
        Player player = new Player();
        player.id = teamPlayer.playerName;
        player.name = teamPlayer.playerName;
        player.teams.add(team);
        DBWrapper.addPlayer(player);
        teamPlayer.playerId = player.id;
        return player;
    }

    public String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    private List<TeamPlayer> readFrom(String filename) throws IOException {
        int id = getResources().getIdentifier(filename, "raw", getPackageName());
        InputStream ins = getResources().openRawResource(id);

        List<TeamPlayer> players = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
        String line = reader.readLine();
        while (line != null){
            String[] data = line.split("\t");
            TeamPlayer teamPlayer = new TeamPlayer();
            teamPlayer.teamName = data[0].trim();
            teamPlayer.playerName = data[1].trim();
            try{
                teamPlayer.number = data[2].trim();
            }catch (Exception ex){}

            players.add(teamPlayer);
            line = reader.readLine();
        }
        return players;
    }


}
