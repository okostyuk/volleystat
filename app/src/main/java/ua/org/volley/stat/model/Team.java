package ua.org.volley.stat.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by userXЗ on 21.11.2016.
 */

public class Team  extends FirebaseRecord{

    public Team() {
    }

    public Team(String name) {
        this.name = name;
        id = name;
    }

    public String name;
    public Map<String, TeamPlayer> players = new HashMap<>();

    public void addPlayer(TeamPlayer teamPlayer){
        teamPlayer.id = "n"+teamPlayer.number;
        players.put(teamPlayer.id, teamPlayer);
    }

    @Override
    public String toString() {
        return name;
    }
}
