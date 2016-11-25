package ua.org.volley.stat.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by userXЗ on 21.11.2016.
 */

public class Player extends FirebaseRecord{
    public String name;
    public long dob;

    public List<Team> teams = new ArrayList<>();
}
