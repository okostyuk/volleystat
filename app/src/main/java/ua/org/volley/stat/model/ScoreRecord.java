package ua.org.volley.stat.model;

import java.util.Map;

/**
 * Created by userXЗ on 22.11.2016.
 */

public class ScoreRecord extends FirebaseRecord{

    public String gameSetId;
    public Integer gameSetNum;
    public String gameId;
    public String teamId;
    public TeamPlayer teamPlayer;
    public String statRecordId;
    public String action;
    public Long time;
    public Map<String, Integer> score;
}
