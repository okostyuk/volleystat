package ua.org.volley.stat;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ua.org.volley.stat.model.FirebaseRecord;
import ua.org.volley.stat.model.Game;
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
    public static final String GAMES = "games";
    public static final String STATS = "stats";

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

    public static Team addTeam(String teamId, String teamName){
        Team team = new Team(teamName);
        team.id = teamId;
        database.getReference(TEAMS).child(teamId).setValue(team);
        return team;
    }

    public static TeamPlayer createTeamPlayer(Team team, Player player, String number){
        TeamPlayer teamPlayer = new TeamPlayer(team, player, number);
        addFirebaseRecord(teamPlayer, TEAM_PLAYERS);
        return teamPlayer;
    }

    public static <T extends FirebaseRecord> void addFirebaseRecord(T record, String name){
        if (record.id == null){
            DatabaseReference teamRef = database.getReference(name).push();
            record.id = teamRef.getKey();
            teamRef.setValue(record);
        }else{
            DatabaseReference teamRef = database.getReference(name).child(record.id);
            teamRef.setValue(record);
        }
    }


    public static void findRecord(String path, String id, ValueEventListener listener) {
        database.getReference(path+"/"+id).addListenerForSingleValueEvent(listener);
    }

    public static void findRecords(String path, ChildEventListener listener){
        database.getReference(path).addChildEventListener(listener);
    }

    public static void updateFirebaseRecord(FirebaseRecord record, String path) {
        database.getReference(path).child(record.id).setValue(record);
    }

    public static void addPlayer(Player player) {
        if (player.id == null){
            player.id = player.name;
        }
        database.getReference(PLAYERS).child(player.id).setValue(player);
    }

    public static Team createDefaultTeam(String teamName) {
        Team team = new Team(teamName);
        team.id = team.name;
        DBWrapper.addTeam(team.name, team.name);
        for (int i=1; i<=12; i++){
            Player player = new Player();
            player.teams.add(team);
            player.name = "Неизвестно";
            player.id = teamName+"-"+i;
            DBWrapper.addPlayer(player);
            TeamPlayer teamPlayer = new TeamPlayer(team, player, String.valueOf(i));
            team.addPlayer(teamPlayer);
        }
        DBWrapper.updateFirebaseRecord(team, DBWrapper.TEAMS);

        return team;
    }
}
