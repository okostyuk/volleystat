package ua.org.volley.stat.model;

/**
 * Created by userXЗ on 21.11.2016.
 */

public class TeamPlayer extends FirebaseRecord {
    public String teamName;
    public String teamId;

    public TeamPlayer() {
    }

    public TeamPlayer(Team team, Player player, String number) {
        playerId = player.id;
        playerName = player.name;
        this.number = number;
    }

    public String playerId;
    public String number;
    public String playerName;
}
