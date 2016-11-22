package ua.org.volley.stat.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by userXЗ on 22.11.2016.
 */

public class GameSet extends FirebaseRecord {
    public String gameId;
    public Integer teamOneScore;
    public Integer teamTwoScore;
    public List<ScoreRecord> scoreRecords = new ArrayList<>();
}
