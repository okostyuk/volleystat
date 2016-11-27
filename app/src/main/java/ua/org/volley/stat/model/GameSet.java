package ua.org.volley.stat.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by userXЗ on 22.11.2016.
 */

public class GameSet extends FirebaseRecord {
    public Long startTime;
    public Long endTime;
    public String gameId;
    public String winnerTeamId;
    public Map<String, Integer> scores = new HashMap<>(2);
    public Map<String,ScoreRecord> scoreRecords = new HashMap<>();

    public GameSet() {
    }

    public GameSet(Team teamOne, Team teamTwo) {
        scores.put(teamOne.id, 0);
        scores.put(teamTwo.id, 0);
        startTime = System.currentTimeMillis();
    }
}
