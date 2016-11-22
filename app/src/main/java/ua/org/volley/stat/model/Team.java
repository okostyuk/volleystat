package ua.org.volley.stat.model;

import java.util.List;

/**
 * Created by userXЗ on 21.11.2016.
 */

public class Team {

    public Team() {
    }

    public Team(String name) {
        this.name = name;
    }

    public String id;
    public String name;
    public List<String> teamPlayers;

    @Override
    public String toString() {
        return name;
    }
}
