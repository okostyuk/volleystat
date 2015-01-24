package com.oleg.activity;

import java.util.List;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.oleg.Game;
import com.oleg.R;
import com.oleg.Team;
import com.oleg.db.DatabaseHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class StatFilterActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	
	Spinner teamSpinner;
	Spinner gameSpinner;
	ArrayAdapter<Team> teamAdapter;
	ArrayAdapter<Game> gameAdapter;
	
	List<Team> teams;
	List<Game> games;
	
	Team team;
	Game game;
	
	Button okButton;
	Button cancelButton;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_filter_layout);
        
        teamSpinner =(Spinner) findViewById(R.id.teamSspinner);
        gameSpinner =(Spinner) findViewById(R.id.gameSpinner);
        okButton = (Button) findViewById(R.id.okButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        
        teams = getHelper().getTeams();//dbAdapter.getTeamsList();
        games = getHelper().getGames();//dbAdapter.getGamesList();
        
        teams.add(new Team());
        games.add(new Game());
        


        teamAdapter = new ArrayAdapter<Team>(this, android.R.layout.select_dialog_item, teams);
        gameAdapter = new ArrayAdapter<Game>(this, android.R.layout.select_dialog_item, games);
        teamSpinner.setAdapter(teamAdapter);
        gameSpinner.setAdapter(gameAdapter);

        teamSpinner.setSelection(teams.size()-1);
        gameSpinner.setSelection(games.size()-1);
        
        
        teamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int id, long arg3) {
	        	games.clear();
	        	team = null;
		        if (id >= teams.size()-1){
		        	team = teams.get(id);
		        }
		        
	        	List<Game> tGames = getHelper().getGames();
	        	for (Game g : tGames){
	        		if (g.getTeam() != null){
	        			games.add(g);
	        		}
	        	}
		        games.add(new Game());
		        gameSpinner.setSelection(games.size()-1);
		        runOnUiThread(new Runnable(){
					public void run() {
						gameAdapter.notifyDataSetChanged();
					}
		        });
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
        
        gameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int id, long arg3) {
				if (id >= games.size()-1)
					game = null;
				else
					game = games.get(id);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        
        okButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				if (team != null)
					intent.putExtra("teamId", team.getId());
				if (game != null)
					intent.putExtra("gameId", game.getId());
				setResult(RESULT_OK, intent);
				finish();
			}
		});
        
        cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
    }
	
}
