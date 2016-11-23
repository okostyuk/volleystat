package ua.org.volley.stat.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by userXЗ on 21.11.2016.
 */

public class Game extends FirebaseRecord {

    public String teamOneId;
    public String teamTwoId;
    public String teamOneName;
    public String teamTwoName;

    public Long dateStart;
    public Long dateEnd;
    public List<GameSet> gameSets = new ArrayList<>();

    public Game() {
    }

    public Game(Team teamOne, Team teamTwo) {
        teamOneId = teamOne.id;
        teamOneName = teamOne.name;
        teamTwoId = teamTwo.id;
        teamTwoName = teamTwo.name;
        dateStart = System.currentTimeMillis();
    }
}
