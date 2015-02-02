package com.oleg.volleystat.activity;


import java.util.List;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.oleg.volleystat.R;
import com.oleg.volleystat.db.DatabaseHelper;
import com.oleg.volleystat.Player;
import com.oleg.volleystat.Team;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class TeamActivity extends OrmLiteBaseActivity<DatabaseHelper>  {

	private List<Team> teams;
	
	private ListView teammatesListView;
	private PlayersAdapter teammatesAdapter;
	//private DBAdapter dbAdapter;
	private List<Player> teammatesList;
	private Button addPlayerButton;
	private Button addTeamButton;
	
	private Spinner teamSpinner;
	private ArrayAdapter<Team> teamAdapter;

	private Dialog playerDialog;
	private Dialog teamDialog;
	private EditText playerName;
	private EditText teamName;
	private Button deleteButton;
	private Button saveButton;
	private Button cancelButton;
	private Button saveTeamButton;
	private Button cancelTeamButton;
	private Player selectedPlayer;
	private TextView errorText;
	
	private Team selectedTeam;

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.players_list);
		
		
		addPlayerButton = (Button) findViewById(R.id.addButton);
		addTeamButton = (Button) findViewById(R.id.addTeamButton);
		teamSpinner = (Spinner) findViewById(R.id.teamsList);
		
		
		addPlayerButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				selectedPlayer = null;
				playerDialog.setTitle("TODO TODO");
				playerName.setText("");
				errorText.setText("");
				playerDialog.show();
				//addTeammate(newTeammateName.getText().toString());
			}
		});
		
		addTeamButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				teamDialog.setTitle(R.string.new_team);
				teamName.setText("");
				teamDialog.show();
			}
		});

		teams = getHelper().getTeams();
		selectedTeam = teams.get(0);
		teammatesList = getHelper().getPlayers(selectedTeam);
		
		teamAdapter = new ArrayAdapter<Team>(this, android.R.layout.select_dialog_item, teams);
		teamSpinner.setAdapter(teamAdapter);
		
    	teammatesAdapter = new PlayersAdapter(this);

        teammatesListView = (ListView) findViewById(R.id.playersList);
        teammatesListView.setAdapter(teammatesAdapter);
        playerDialog = new Dialog(this);
        playerDialog.setContentView(R.layout.player_dialog);
        
        teamDialog = new Dialog(this);
        teamDialog.setContentView(R.layout.team_dialog);

        playerName = (EditText) playerDialog.findViewById(R.id.name);
        deleteButton = (Button) playerDialog.findViewById(R.id.deleteButton);
        errorText = (TextView) playerDialog.findViewById(R.id.errorText);
        saveButton = (Button) playerDialog.findViewById(R.id.saveButton);
        cancelButton = (Button) playerDialog.findViewById(R.id.cancelButton);
        
        teamName = (EditText) teamDialog.findViewById(R.id.teamName);
        saveTeamButton = (Button) teamDialog.findViewById(R.id.saveButton);
        cancelTeamButton = (Button) teamDialog.findViewById(R.id.cancelButton);
        
        deleteButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (selectedPlayer == null)
					return;
				
				getHelper().removePlayer(selectedPlayer);
				teammatesList.remove(selectedPlayer);
				
				playerDialog.dismiss();
				runOnUiThread(new Runnable(){
					public void run() {
						teammatesAdapter.notifyDataSetChanged();
					}
				});

			}
		});
        
        saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String name = playerName.getText().toString();
				if (name.trim().isEmpty())
					return;
				boolean exist = false;
				for (Player p : teammatesList){
					if (p.getName().equalsIgnoreCase(name)){
						exist = true;
						break;
					}
				}
				if (!exist){
					if (selectedPlayer == null)
						addTeammate(name, selectedTeam);
					else
						updateTeamate(selectedPlayer, name);
					playerDialog.dismiss();
					runOnUiThread(new Runnable(){
						public void run() {
							teammatesAdapter.notifyDataSetChanged();
						}
					});
				}else{
					errorText.setText("TODO TODO TODO TODO TODO TODO");
				}
			}

		});
        
        cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				playerDialog.dismiss();
			}
		});
        
        saveTeamButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String name = teamName.getText().toString().trim();
				if (name.isEmpty())
					return;
				Team team = new Team();
				team.setName(name);
				getHelper().createOrUpdateTeam(team);
				teams.add(team);
				teamDialog.dismiss();
				runOnUiThread(new Runnable(){
					public void run() {
						teamAdapter.notifyDataSetChanged();
					}
				});
			}
		});
        
        cancelTeamButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				teamDialog.dismiss();
			}
		});
        
        teamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int id, long arg3) {
				selectedTeam = teams.get(id);
				teammatesList = getHelper().getPlayers(selectedTeam);
				runOnUiThread(new Runnable(){
					public void run() {
						teammatesAdapter.notifyDataSetChanged();
					}
				});
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
        
	}
	
	private void updateTeamate(Player player, String name) {
		player.setName(name);
		getHelper().createOrUpdatePlayer(player);
	}
	
	private void addTeammate(String name, Team team){
		Player player = new Player();
		player.setName(name);
		player.setTeam(team);
		player.setValid();
		getHelper().createOrUpdatePlayer(player);
		teammatesList.add(player);
	}
	
	private class PlayersAdapter extends BaseAdapter{
		Context context;
		ImageButton editButton;
		
		public PlayersAdapter(Context c){
			super();
			context = c;
		}

		public int getCount() {
			return teammatesList.size();
		}

		public Object getItem(int position) {
			return teammatesList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null){
				convertView = View.inflate(context, R.layout.teammate, null);
			}
			TextView v = (TextView) convertView.findViewById(R.id.name);
			v.setText(teammatesList.get(position).getName());
			
			
			editButton=(ImageButton) convertView.findViewById(R.id.editButton);
			editButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					selectedPlayer = teammatesList.get(position);
					playerDialog.setTitle("TODO " + selectedPlayer.getName());
					playerName.setText(selectedPlayer.getName());
					playerDialog.show();
				}
			});

			return convertView;
		}
		
	}

}
