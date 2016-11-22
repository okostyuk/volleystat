package ua.org.volley.stat.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ua.org.volley.stat.DBWrapper;
import ua.org.volley.stat.R;
import ua.org.volley.stat.adapters.TeamSelectAdapter;
import ua.org.volley.stat.model.Team;

public class SelectTeamsActivity extends AppCompatActivity {


    private static final String TAG = "SelectTeamsActivity";
    private TeamSelectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_teams);


        RecyclerView list = (RecyclerView) findViewById(R.id.teamsList);
        list.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference teams = FirebaseDatabase.getInstance().getReference(DBWrapper.TEAMS);

        teams.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        });

        adapter = new TeamSelectAdapter(teams.orderByValue(), Team.class);
        list.setAdapter(adapter);
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
}
