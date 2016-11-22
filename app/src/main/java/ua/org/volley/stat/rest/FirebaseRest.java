package ua.org.volley.stat.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ua.org.volley.stat.model.Team;
import ua.org.volley.stat.model.TeamPlayer;

/**
 * Created by userXЗ on 22.11.2016.
 */

public interface FirebaseRest {
    @GET("team_players.json")
    Call<Map<String, TeamPlayer>> loadPlayersFiltered(
            @Query("orderBy") String fieldName,
            @Query("equalTo") String fieldValue);

    @GET("teams.json")
    Call<Map<String, Team>> loadTeams();
}
