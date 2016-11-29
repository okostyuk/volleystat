package ua.org.volley.stat.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
import ua.org.volley.stat.adapters.TeamSelectAdapterFirebase;
import ua.org.volley.stat.adapters.TeamSelectAdapterRest;
import ua.org.volley.stat.model.Team;
import ua.org.volley.stat.rest.FirebaseRest;

public class SelectTeamsActivity extends AppCompatActivity implements Callback<Map<String, Team>> {


    private static final String TAG = "SelectTeamsActivity";
    private TeamSelectAdapterRest adapter;
    private RecyclerView teamsList;
    FirebaseRest restService;
    private View addTeamButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_teams);

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


        teamsList = (RecyclerView) findViewById(R.id.teamsList);
        teamsList.setLayoutManager(new LinearLayoutManager(this));

        //DatabaseReference teams = FirebaseDatabase.getInstance().getReference(DBWrapper.TEAMS);
        //adapter = new TeamSelectAdapterFirebase(teams.orderByValue(), Team.class);

        setTitle("Loading teams...");
        addTeamButton = findViewById(R.id.addTeamButton);
        addTeamButton.setEnabled(false);
        restService.loadTeams().enqueue(this);


        addTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText teamnameText = new EditText(SelectTeamsActivity.this);
                teamnameText.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
                teamnameText.setHint("Введите название");
                new AlertDialog.Builder(SelectTeamsActivity.this)
                        .setTitle("Новая команда")
                        .setView(teamnameText)
                        .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int btnId) {
                                String teamName = teamnameText.getText().toString();
                                Team team = new Team(teamName);
                                DBWrapper.addFirebaseRecord(team, DBWrapper.TEAMS);
                                adapter.addTeam(team);
                            }
                        })
                        .setNegativeButton("Отмена", null)
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_tesams_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_teams_selected:
                if (adapter.teamOneId == null || adapter.teamTwoId == null)
                    return false;

                Intent intent = new Intent(this, GameActivity.class);
                intent.putExtra(GameActivity.TEAM_ONE, adapter.teamOneId);
                intent.putExtra(GameActivity.TEAM_TWO, adapter.teamTwoId);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(Call<Map<String, Team>> call, Response<Map<String, Team>> response) {
        if (response.code() == 200){
            List<Team> teams = new ArrayList<>();
            if (response.body() != null){
                teams = new ArrayList<>(response.body().values());
            }
            adapter = new TeamSelectAdapterRest(teams);
            teamsList.setAdapter(adapter);
            setTitle("Выберите 2 команды");
            addTeamButton.setEnabled(true);
        }
    }

    @Override
    public void onFailure(Call<Map<String, Team>> call, Throwable t) {
        Log.e(TAG, "onFailure: ", t);
        setTitle("Error");
    }
}
