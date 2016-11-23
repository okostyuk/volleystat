package ua.org.volley.stat.activity;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ua.org.volley.stat.DBWrapper;
import ua.org.volley.stat.R;
import ua.org.volley.stat.adapters.GamePlayersAdapter;
import ua.org.volley.stat.model.Game;
import ua.org.volley.stat.model.GameSet;
import ua.org.volley.stat.model.ScoreRecord;
import ua.org.volley.stat.model.StatRecord;
import ua.org.volley.stat.model.StatRecordType;
import ua.org.volley.stat.model.Team;
import ua.org.volley.stat.model.TeamPlayer;
import ua.org.volley.stat.rest.FirebaseRest;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {


    public static final String TEAM_ONE = "teamOne";
    public static final String TEAM_TWO = "teamTwo";
    private static final String TAG = "GameActivity";

    Game game;
    GameSet gameSet;
    StatRecordType selectedType = null;
    Team[] teams = new Team[2];

    boolean teamSwaped = false;
    GamePlayersAdapter gamePlayersAdapter;
    FirebaseRest restService;
    RecyclerView playersList;
    List<TextView> actionButtons = new ArrayList<>();

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

        findViewById(R.id.incLeft).setOnClickListener(this);
        findViewById(R.id.incRight).setOnClickListener(this);


        TextView serveBtn = (TextView) findViewById(R.id.serve);
        TextView passBtn = (TextView) findViewById(R.id.pass);
        TextView attackBtn = (TextView) findViewById(R.id.attack);
        TextView digBtn = (TextView) findViewById(R.id.dig);
        TextView setBtn = (TextView) findViewById(R.id.set);
        TextView blockBtn = (TextView) findViewById(R.id.block);

        serveBtn.setOnClickListener(this);
        passBtn.setOnClickListener(this);
        attackBtn.setOnClickListener(this);
        digBtn.setOnClickListener(this);
        setBtn.setOnClickListener(this);
        blockBtn.setOnClickListener(this);

        actionButtons.add(serveBtn);
        actionButtons.add(passBtn);
        actionButtons.add(attackBtn);
        actionButtons.add(digBtn);
        actionButtons.add(setBtn);
        actionButtons.add(blockBtn);

        findViewById(R.id.neg).setOnClickListener(this);
        findViewById(R.id.neutral).setOnClickListener(this);
        findViewById(R.id.pos).setOnClickListener(this);

        String teamOneId = getIntent().getStringExtra(TEAM_ONE);
        String teamTwoId = getIntent().getStringExtra(TEAM_TWO);

        loadTeam(0, teamOneId);
        loadTeam(1, teamTwoId);
    }

    private void loadTeam(final int teamIndex, String teamId) {
        setTitle("Loading teams...");
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
        if (teams[0] != null && teams[1] != null){
            setTitle("Loading players...");
            game = createGame();
            loadTeamPlayers(teams[0].id);
        }
    }

    private Game createGame() {
        Game newGame = new Game(teams[0], teams[1]);
        DBWrapper.addFirebaseRecord(newGame, DBWrapper.GAMES);
        return newGame;
    }

    private void loadTeamPlayers(String teamId) {
        restService.loadPlayersFiltered("\"teamId\"", "\""+teamId+"\"").enqueue(new Callback<Map<String, TeamPlayer>>() {
            @Override
            public void onResponse(Call<Map<String, TeamPlayer>> call, Response<Map<String, TeamPlayer>> response) {
                if (response.code() == 200){
                    List<TeamPlayer> players = new ArrayList<>(response.body().values());
                    gamePlayersAdapter = new GamePlayersAdapter(players);
                    playersList.setAdapter(gamePlayersAdapter);
                    updateTitle();
                }
            }

            @Override
            public void onFailure(Call<Map<String, TeamPlayer>> call, Throwable t) {

            }
        });
    }

    private void updateTitle() {
        String leftTeamScore = getTeamScore(teams[0]);
        String rightTeamScore = getTeamScore(teams[1]);
        if (teamSwaped)
            setTitle(rightTeamScore + " vs " + leftTeamScore);
        else
            setTitle(leftTeamScore + " vs " + rightTeamScore);
    }

    private String getTeamScore(Team team) {
        if (team == null) return null;
        if (game == null) return team.name;
        int winnedSets = 0;
        int setScored = 0;
        for (GameSet set : game.gameSets){
            if (team.id.equals(set.winnerTeamId)){
                winnedSets++;
            }else if (set.winnerTeamId == null){
                setScored = set.scores.get(team.id);
            }
        }
        StringBuilder builder = new StringBuilder();
        builder
                .append(team.name)
                .append(" ").append(setScored)
                .append("(").append(winnedSets).append(")")
                ;
        return builder.toString();
    }

    MenuItem start, swap, undo, viewLog;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_menu, menu);
        start = menu.findItem(R.id.action_start);
        swap = menu.findItem(R.id.action_swap);
        undo = menu.findItem(R.id.action_undo);
        viewLog = menu.findItem(R.id.action_view_log);

        undo.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_start:
                startNewSet();
                start.setVisible(false);
                swap.setVisible(false);
                undo.setVisible(true);
                break;
            case R.id.action_view_log:
                break;
            case R.id.action_swap:
                teamSwaped = !teamSwaped;
                break;
        }
        updateTitle();
        return super.onOptionsItemSelected(item);
    }

    private void startNewSet() {
        GameSet newSet = new GameSet(teams[0], teams[1]);
        game.gameSets.add(newSet);
        gameSet = newSet;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.attack:
            case R.id.dig:
            case R.id.block:
            case R.id.set:
            case R.id.serve:
            case R.id.pass:
                for (TextView btn : actionButtons){
                    btn.setCompoundDrawables(null, null, null, null);
                }
                selectedType = btnIdToActionType(view.getId());

                Drawable selected = getResources().getDrawable(R.drawable.ic_done_black_24dp);
                ((TextView)view).setCompoundDrawables(null, null, selected, null);
                break;
            case R.id.pos:
            case R.id.neg:
            case R.id.neutral:
                StatRecord statRecord = addStatRecord(view.getId());
                addScoreRecord(teams[0].id, statRecord);
                break;
            case R.id.incLeft:
                addScoreRecord(teams[teamSwaped?1:0].id, null);
                break;
            case R.id.incRight:
                addScoreRecord(teams[teamSwaped?0:1].id, null);
                break;

        }
    }

    private StatRecordType btnIdToActionType(int btnId) {
        switch (btnId){
            case R.id.serve: return StatRecordType.SERVE;
            case R.id.pass: return StatRecordType.PASS;
            case R.id.set: return StatRecordType.SET;
            case R.id.block: return StatRecordType.BLOCK;
            case R.id.attack: return StatRecordType.ATTACK;
            case R.id.dig: return StatRecordType.DIG;
            default: return null;
        }
    }

    private void addScoreRecord(String teamId, @Nullable StatRecord statRecord) {
        int curScore = gameSet.scores.get(teamId);
        gameSet.scores.put(teamId, curScore+1);
        ScoreRecord scoreRecord = new ScoreRecord();
        scoreRecord.time= System.currentTimeMillis();
        scoreRecord.gameId = game.id;
        scoreRecord.teamId = teamId;
        int setNum = game.gameSets.indexOf(gameSet)+1;
        scoreRecord.gameSetId = gameSet.id;
        scoreRecord.gameSetNum = setNum;
        if (statRecord != null){
            scoreRecord.playerId = statRecord.playerId;
            scoreRecord.statRecordId = statRecord.id;
        }
        scoreRecord.score = gameSet.scores.get(teamId);

        gameSet.scoreRecords.add(scoreRecord);
        DBWrapper.saveFirebaseRecord(game, DBWrapper.GAMES);
        updateTitle();
    }

    private StatRecord addStatRecord(int btnId) {
        StatRecord statRecord = new StatRecord();
        statRecord.gameId = game.id;
        statRecord.playerId = gamePlayersAdapter.getSelectedPlayer().playerId;
        statRecord.setId = gameSet.id;
        statRecord.actionType = selectedType.name();
        if (btnId == R.id.pos)
            statRecord.value = 1;
        else if (btnId == R.id.neutral)
            statRecord.value = 0;
        else if (btnId == R.id.neg)
            statRecord.value = -1;

        DBWrapper.addFirebaseRecord(statRecord, DBWrapper.STATS);
        return statRecord;

    }
}
