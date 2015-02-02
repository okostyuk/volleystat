package com.oleg.volleystat.activity;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.oleg.volleystat.Game;
import com.oleg.volleystat.Items;
import com.oleg.volleystat.Player;
import com.oleg.volleystat.R;
import com.oleg.volleystat.Team;
import com.oleg.volleystat.db.DatabaseHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FilterActivity extends OrmLiteBaseActivity<DatabaseHelper> implements OnClickListener{
	
	Team team;
	Game game;
	Player player;
	
	Button teamButton;
	Button gameButton;
	Button playerButton;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_layout);
        
        teamButton = (Button) findViewById(R.id.teamButton);
        gameButton = (Button) findViewById(R.id.gameButton);
        playerButton = (Button) findViewById(R.id.playerButton);

        getData(getIntent());
        
        teamButton.setOnClickListener(this);
    }
    
    private void getData(Intent data){
    	if (data == null || data.getExtras() == null)
    		return;

    	int id = data.getExtras().getInt("id");
    	
    	int teamId = data.getExtras().getInt("teamId", -2);
		if (teamId < -1){
		}else if(teamId < 0){
			teamButton.setText(R.string.all);
		}else{
			team = getHelper().getTeam(teamId);
			teamButton.setText(team.getName());
		}
		
		int gameId = data.getExtras().getInt("gameId", -2);
		if (gameId < -1){
		}else if(gameId < 0){
			gameButton.setText(R.string.all);
		}else{
			game = getHelper().getGame(gameId);
			gameButton.setText(game.toString());//TODO
		}

		int playerId = data.getExtras().getInt("gameId", -2);
		if (playerId < -1){
		}else if(playerId < 0){
			playerButton.setText(R.string.all);
		}else{
			player = getHelper().getPlayer(playerId);
			playerButton.setText(team.getName());
		}
    }
    
    
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null || data.getExtras() == null)
			return;

		Items item = Items.values()[requestCode];
		
		Log.d("result", "resultCode = " + requestCode);
		Log.d("result", "requestCode = " + requestCode);
		Log.d("result", "item = " + item.name());
		
		int id = data.getExtras().getInt("id", -2);
		Log.d("result", "id = " + id);
		switch(item){
		case TEAM:
			if (id == -1){
				team = null;
				teamButton.setText(R.string.all);
			}else if (id >= 0){
				team = getHelper().getTeam(id);
				teamButton.setText(team.getName());
			}
			break;
		case GAME:
			if (id == -1){
				game = null;
				gameButton.setText(R.string.all);
			}else if (id >= 0){
				game = getHelper().getGame(id);
				gameButton.setText(game.toString());
			}
			break;
		case PLAYER:
			if (id == -1){
				player = null;
				playerButton.setText(R.string.all);
			}else if (id >= 0){
				player = getHelper().getPlayer(id);
				playerButton.setText(player.getName());
			}
			break;
		}
		
		//getData(data);
	}

	public void onClick(View v) {
		Items requestCode = null;
		int paramValue = -1;
		
		if (v.getId() == R.id.teamButton){
			requestCode = Items.TEAM;
			if (team != null){
				paramValue = team.getId();
			}
		}

		if (v.getId() == R.id.gameButton){
			requestCode = Items.GAME;
			if (game != null){
				paramValue = team.getId();
			}
		}

		if (v.getId() == R.id.gameButton){
			requestCode = Items.GAME;
			if (game != null){
				paramValue = team.getId();
			}
		}
		
		
		Intent intent = new Intent(this, SimpleListActivity.class);
		intent.putExtra("id", paramValue);
		intent.putExtra("type", requestCode);
		startActivityForResult(intent, requestCode.ordinal());
	}

}
