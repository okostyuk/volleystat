package ua.org.volley.stat.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
        restService.loadTeams().enqueue(this);
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
        }
    }

    @Override
    public void onFailure(Call<Map<String, Team>> call, Throwable t) {
        Log.e(TAG, "onFailure: ", t);
        setTitle("Error");
    }
}
