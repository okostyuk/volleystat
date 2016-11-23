package ua.org.volley.stat.model;

import java.util.List;

/**
 * Created by userXЗ on 21.11.2016.
 */

public class Team  extends FirebaseRecord{

    public Team() {
    }

    public Team(String name) {
        this.name = name;
    }

    public String name;
    public List<TeamPlayer> teamPlayers;

    @Override
    public String toString() {
        return name;
    }
}
