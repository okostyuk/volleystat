package ua.org.volley.stat.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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

        TeamSelectAdapter adapter = new TeamSelectAdapter(teams.orderByValue(), Team.class);
        list.setAdapter(adapter);
    }
}
