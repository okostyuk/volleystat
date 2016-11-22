package ua.org.volley.stat.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by userXЗ on 21.11.2016.
 */

public class Game extends FirebaseRecord {

    public String firstTeamId;
    public String secondTeamId;
    public long dateStart;
    public Long dateEnd;
    List<GameSet> gameSets = new ArrayList<>();

}
