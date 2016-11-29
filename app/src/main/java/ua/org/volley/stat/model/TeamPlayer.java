package ua.org.volley.stat.model;

import com.google.firebase.database.Exclude;

/**
 * Created by userXЗ on 21.11.2016.
 */

public class TeamPlayer extends FirebaseRecord {

    public String teamName;
    public String teamId;

    public TeamPlayer() {
    }

    public TeamPlayer(Team team, Player player, String number) {
        if (player != null) {
            playerId = player.id;
            playerName = player.name;
        }
        this.number = number;
    }

    public String playerId;
    public String number;
    public String playerName;

    @Exclude
    public Team team;

}
