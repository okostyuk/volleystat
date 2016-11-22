package ua.org.volley.stat;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ua.org.volley.stat.model.FirebaseRecord;
import ua.org.volley.stat.model.Player;
import ua.org.volley.stat.model.Team;
import ua.org.volley.stat.model.TeamPlayer;

/**
 * Created by userXЗ on 21.11.2016.
 */

public class DBWrapper {

    public static final String TEAM_PLAYERS = "team_players";
    public static final String TEAMS = "teams";
    public static final String PLAYERS = "players";

    private static FirebaseDatabase database;


    public static void init(Context context){
        FirebaseApp.initializeApp(context);
        database = FirebaseDatabase.getInstance();
    }

    public static Player createPlayer(String name){
        Player player = new Player();
        DatabaseReference playerRef = database.getReference(PLAYERS).push();
        player.id = playerRef.getKey();
        player.name = name;
        playerRef.setValue(player);
        return player;
    }

    public static Team createTeam(String name){
        Team team = new Team(name);
        DatabaseReference teamRef = database.getReference(TEAMS).push();
        team.id = teamRef.getKey();
        teamRef.setValue(team);
        return team;
    }

    public static TeamPlayer createTeamPlayer(Team team, Player player, int number){
        TeamPlayer teamPlayer = new TeamPlayer(team, player, number);
        saveFirebaseRecord(teamPlayer, TEAM_PLAYERS);
        return teamPlayer;
    }

    public static <T extends FirebaseRecord> void saveFirebaseRecord(T record, String name){
        DatabaseReference teamRef = database.getReference(name).push();
        record.id = teamRef.getKey();
        teamRef.setValue(record);
    }

    public static void findRecord(String path, String id, ValueEventListener listener) {
        database.getReference(path+"/"+id).addListenerForSingleValueEvent(listener);
    }

    public static void findRecords(String path, ChildEventListener listener){
        database.getReference(path).addChildEventListener(listener);
    }
}
