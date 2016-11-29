package ua.org.volley.stat.activity;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import ua.org.volley.stat.adapters.EditTeamAdapter;
import ua.org.volley.stat.adapters.GamePlayersAdapter;
import ua.org.volley.stat.model.Game;
import ua.org.volley.stat.model.GameSet;
import ua.org.volley.stat.model.ScoreRecord;
import ua.org.volley.stat.model.StatRecord;
import ua.org.volley.stat.model.StatRecordType;
import ua.org.volley.stat.model.Team;
import ua.org.volley.stat.model.TeamPlayer;
import ua.org.volley.stat.rest.FirebaseRest;

public class GameActivity extends AppCompatActivity
        implements View.OnClickListener, FirebaseAuth.AuthStateListener {

    private static final SimpleDateFormat scoreTimeFormat = new SimpleDateFormat("HH-mm-ss");


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
    List<RadioButton> actionButtons = new ArrayList<>();
    public View radioButtons;
    View scoreButtons;
    TextView score;
    View incLeft, incRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBWrapper.init(this);
        setContentView(R.layout.activity_game);
        playersList = (RecyclerView) findViewById(R.id.playersList);
        playersList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        playersList.setVisibility(View.INVISIBLE);

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

        incLeft = findViewById(R.id.incLeft);
        incLeft.setOnClickListener(this);
        incRight = findViewById(R.id.incRight);
        incRight.setOnClickListener(this);
        incRight.setEnabled(false);
        incLeft.setEnabled(false);

        radioButtons = findViewById(R.id.radioButtons);
        scoreButtons = findViewById(R.id.scoreButtons);
        score = (TextView) findViewById(R.id.score);


        RadioButton serveBtn = (RadioButton) findViewById(R.id.serve);
        RadioButton passBtn = (RadioButton) findViewById(R.id.pass);
        RadioButton attackBtn = (RadioButton) findViewById(R.id.attack);
        RadioButton digBtn = (RadioButton) findViewById(R.id.dig);
        RadioButton setBtn = (RadioButton) findViewById(R.id.set);
        RadioButton blockBtn = (RadioButton) findViewById(R.id.block);

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

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        if (mAuth != null) mAuth.removeAuthStateListener(this);
        super.onStop();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        Log.d(TAG, "onAuthStateChanged: " + firebaseAuth.getCurrentUser());
    }

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


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
            start.setVisible(true);
            swap.setVisible(true);
            game = createGame();

            EditTeamAdapter adapter = new EditTeamAdapter(teams[0]);
            playersList.setAdapter(adapter);
            playersList.setVisibility(View.VISIBLE);

            updateTitle();
        }
    }

    private Game createGame() {
        Game newGame = new Game(teams[0], teams[1]);
        newGame.uid = mAuth.getCurrentUser().getUid();
        newGame.email = mAuth.getCurrentUser().getEmail();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        newGame.id = fmt.format(new Date())+"_"+teams[0].id+"_vs_"+teams[1].id;
        DBWrapper.addFirebaseRecord(newGame, DBWrapper.GAMES);
        return newGame;
    }

    private void updateTitle() {
        if (teamSwaped) setTitle(teams[1].name + " vs " + teams[0].name);
        else setTitle(teams[0].name + " vs " + teams[1].name);
    }


    MenuItem start, swap, undo, viewLog;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_menu, menu);
        start = menu.findItem(R.id.action_start);
        swap = menu.findItem(R.id.action_swap);
        undo = menu.findItem(R.id.action_undo);
        viewLog = menu.findItem(R.id.action_view_log);

        start.setVisible(false);
        swap.setVisible(false);
        undo.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_start:
                if (teams[0].players.size() < 6){
                    Toast.makeText(this, "Нужно 6 игроков", Toast.LENGTH_SHORT).show();
                    return true;
                }

                gamePlayersAdapter = new GamePlayersAdapter(GameActivity.this, teams[0].players.values());
                playersList.setAdapter(gamePlayersAdapter);
                startNewSet();
                playersList.setVisibility(View.VISIBLE);
                start.setVisible(false);
                swap.setVisible(false);
                undo.setVisible(true);
                incRight.setEnabled(true);
                incLeft.setEnabled(true);
                updateScore();
                break;
            case R.id.action_view_log:
                break;
            case R.id.action_swap:
                swapTeams();
                break;
        }
        //updateTitle();
        return super.onOptionsItemSelected(item);
    }

    private void swapTeams() {
        teamSwaped = !teamSwaped;
        updateTitle();
    }

    private void startNewSet() {
        GameSet newSet = new GameSet(teams[0], teams[1]);
        newSet.id = String.valueOf(game.gameSets.size());
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
                for (RadioButton btn : actionButtons){
                    btn.setChecked(false);
                }
                scoreButtons.setVisibility(View.VISIBLE);
                ((RadioButton)view).setChecked(true);
                selectedType = btnIdToActionType(view.getId());
                break;
            case R.id.pos:
                StatRecord statRecord = addStatRecord(view.getId());
                addScoreRecord(true, statRecord);
                gamePlayersAdapter.setSelected(-1);
                radioButtons.setVisibility(View.INVISIBLE);
                scoreButtons.setVisibility(View.INVISIBLE);
                break;
            case R.id.neg:
                statRecord = addStatRecord(view.getId());
                addScoreRecord(false, statRecord);
                gamePlayersAdapter.setSelected(-1);
                radioButtons.setVisibility(View.INVISIBLE);
                scoreButtons.setVisibility(View.INVISIBLE);
                break;
            case R.id.neutral:
                addStatRecord(view.getId());
                gamePlayersAdapter.setSelected(-1);
                radioButtons.setVisibility(View.INVISIBLE);
                scoreButtons.setVisibility(View.INVISIBLE);
                break;
            case R.id.incLeft:
                addScoreRecord(!teamSwaped, null);
                break;
            case R.id.incRight:
                addScoreRecord(teamSwaped, null);
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

    private void addScoreRecord(boolean myTeam, @Nullable StatRecord statRecord) {
        String teamId;
        Team team;
        if (myTeam) {
            teamId = teams[0].id;
            team = teams[0];
        }else{
            team = teams[1];
            teamId = teams[1].id;
        }

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
        Integer team1Score = gameSet.scores.get(teams[0].id);
        Integer team2Score = gameSet.scores.get(teams[1].id);
        scoreRecord.score = gameSet.scores.get(teamId);
        String key = scoreTimeFormat.format(new Date(scoreRecord.time))
                +"_"+team.name
                +"_"+team1Score+"-"+team2Score;
        gameSet.scoreRecords.put(key, scoreRecord);
        Team winner = getWinner();
        if (winner != null){
            gameSet.winnerTeamId = winner.id;
            int oldScore = game.setScores.get(winner.id);
            game.setScores.put(winner.id, oldScore+1);

            finishSet(winner);
        }
        DBWrapper.updateFirebaseRecord(game, DBWrapper.GAMES);

        updateScore();
    }

    private void finishSet(Team winner) {
        swapTeams();
        //gamePlayersAdapter = new GamePlayersAdapter(GameActivity.this, teams[0].players.values());
        EditTeamAdapter adapter = new EditTeamAdapter(teams[0]);
        playersList.setAdapter(adapter);
        //startNewSet();
        playersList.setVisibility(View.VISIBLE);
        start.setVisible(true);
        swap.setVisible(true);
        undo.setVisible(false);
        incRight.setEnabled(false);
        incLeft.setEnabled(false);

        radioButtons.setVisibility(View.GONE);
        scoreButtons.setVisibility(View.GONE);
    }

    private Team getWinner() {
        Integer score1 = gameSet.scores.get(teams[0].id);
        Integer score2 = gameSet.scores.get(teams[1].id);

        if (score1.compareTo(25) >= 0 && (score1 - score2) >= 2)
            return teams[0];

        if (score2.compareTo(25) >= 0 && (score2 - score1) >= 2)
            return teams[1];

        return null;
    }

    private void updateScore() {
        if (teamSwaped){
            score.setText(
                    "(" + game.setScores.get(teams[1].id)+ ")" +
                    " " + gameSet.scores.get(teams[1].id) +
                            " : " +
                            gameSet.scores.get(teams[0].id) +
                            " (" + game.setScores.get(teams[0].id)+ ")"
            );
        }else{
            score.setText(
                    "(" + game.setScores.get(teams[0].id)+ ")" +
                            " " + gameSet.scores.get(teams[0].id) +
                            " : " +
                            gameSet.scores.get(teams[1].id) +
                            " (" + game.setScores.get(teams[1].id)+ ")"
            );
        }
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
