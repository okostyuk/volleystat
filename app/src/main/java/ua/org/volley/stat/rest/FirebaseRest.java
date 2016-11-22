package ua.org.volley.stat.rest;

import com.google.gson.JsonElement;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ua.org.volley.stat.model.Team;
import ua.org.volley.stat.model.TeamPlayer;

/**
 * Created by userXЗ on 22.11.2016.
 */

public interface FirebaseRest {
    @GET("teams.json")
    Call<List<Team>> loadTeams();

    @GET("team_players.json")
    Call<JsonElement> loadPlayersFiltered(
            @Query("orderBy") String fieldName,
            @Query("equalTo") String fieldValue);
}
