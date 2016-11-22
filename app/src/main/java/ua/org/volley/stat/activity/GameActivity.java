package ua.org.volley.stat.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ua.org.volley.stat.BaseChildEventListener;
import ua.org.volley.stat.DBWrapper;
import ua.org.volley.stat.R;
import ua.org.volley.stat.adapters.GamePlayersAdapter;
import ua.org.volley.stat.model.Game;
import ua.org.volley.stat.model.Team;
import ua.org.volley.stat.model.TeamPlayer;
import ua.org.volley.stat.rest.FirebaseRest;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {


    public static final String TEAM_ONE = "teamOne";
    public static final String TEAM_TWO = "teamTwo";
    private static final String TAG = "GameActivity";
    Game game;
    Team[] teams = new Team[2];

    GamePlayersAdapter gamePlayersAdapter;
    FirebaseRest restService;
    RecyclerView playersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        playersList = (RecyclerView) findViewById(R.id.playersList);
        playersList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://volleystat-5bc1c.firebaseio.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        restService = retrofit.create(FirebaseRest.class);

        findViewById(R.id.decLeft).setOnClickListener(this);
        findViewById(R.id.decRight).setOnClickListener(this);
        findViewById(R.id.incLeft).setOnClickListener(this);
        findViewById(R.id.incRight).setOnClickListener(this);

        findViewById(R.id.put).setOnClickListener(this);
        findViewById(R.id.pass).setOnClickListener(this);
        findViewById(R.id.attack).setOnClickListener(this);
        findViewById(R.id.receive).setOnClickListener(this);
        findViewById(R.id.block).setOnClickListener(this);

        findViewById(R.id.neg).setOnClickListener(this);
        findViewById(R.id.neutral).setOnClickListener(this);
        findViewById(R.id.pos).setOnClickListener(this);

        String teamOneId = getIntent().getStringExtra(TEAM_ONE);
        String teamTwoId = getIntent().getStringExtra(TEAM_TWO);

        loadTeam(0, teamOneId);
        loadTeam(1, teamTwoId);
    }

    private void loadTeam(final int teamIndex, String teamId) {
        DBWrapper.findRecord(DBWrapper.TEAMS, teamId, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "findTeam onDataChange: ");
                teams[teamIndex] = dataSnapshot.getValue(Team.class);
                teamLoaded();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        });

    }

    private synchronized void teamLoaded() {
        setTitle(String.valueOf(teams[0]) + " vs " + String.valueOf(teams[1]));
        if (teams[0] != null && teams[1] != null){
            loadTeamPlayers(teams[0].id);
        }
    }

    private void loadTeamPlayers(String teamId) {
        restService.loadPlayersFiltered("\"teamId\"", "\""+teamId+"\"").enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.code() == 200){
                    JsonElement json = response.body();
                    Set<Map.Entry<String, JsonElement>> set = json.getAsJsonObject().entrySet();
                    for (Map.Entry<String, JsonElement> item : set){
                        item.getValue()
                    }
                    gamePlayersAdapter = new GamePlayersAdapter(players);
                    playersList.setAdapter(gamePlayersAdapter);
                }else{
                    try {
                        Toast.makeText(GameActivity.this, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: ", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_view_log:
                //startActivity(new Intent(this, GameActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

    }
}
