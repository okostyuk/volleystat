package com.oleg.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.oleg.R;
import com.oleg.R.id;
import com.oleg.R.layout;
import com.oleg.R.string;
import com.oleg.Team;
import com.oleg.db.DatabaseHelper;
import com.oleg.Player;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PositionsActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	public static final String TEAM = "team";

	PositionsAdapter positionsAdapter;
	ArrayAdapter<String> playersAdapter;
	List<Player> teamPlayersList;
	ListView playersListView;
	//DBAdapter dbAdapter;
	Button applyTeamButton;
	Button startGameButton;
	List<Player> gamePlayersList = new ArrayList<Player>();
	Boolean isTeamReady = false;
	String[] playersArray;
	Team team;
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.positions_view);
		playersListView = (ListView) findViewById(R.id.List);
		applyTeamButton = (Button) findViewById(R.id.apply);
		startGameButton = (Button) findViewById(R.id.start);
		startGameButton.setEnabled(false);
		team = getHelper().getTeam(getIntent().getExtras().getInt(VolleyStatActivity.TEAM_ID));
		
		applyTeamButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (isTeamReady){
					playersListView.setAdapter(playersAdapter);
					applyTeamButton.setText(R.string.apply_team);
					isTeamReady = false;
				}else{
					startGameButton.setEnabled(gamePlayersList.size()==6);
					SparseBooleanArray checked = playersListView
							.getCheckedItemPositions();
					int size = playersAdapter.getCount();//checked.size();
					int i = 0;
					gamePlayersList.clear();
					while (i <= size) {
						if (checked.get(i)) {
							//teamPlayersList.get(i).setPosition(gamePlayersList.size()+1);
							gamePlayersList.add(teamPlayersList.get(i));
						}
						i++;
					}
					if (gamePlayersList.size() == 6){
						/*startGameButton.setEnabled(true);
						playersListView.setAdapter(positionsAdapter);
						applyTeamButton.setText("Сменить состав");
						isTeamReady = true;*/
						ArrayList<Integer> teamIDsArrayList = new ArrayList<Integer>();
						for(Player p : gamePlayersList){
							teamIDsArrayList.add(p.getId());
						}
						Intent intent = new Intent(PositionsActivity.this, GameActivity.class);
						intent.putIntegerArrayListExtra(TEAM, teamIDsArrayList);
						intent.putExtra(VolleyStatActivity.TEAM_ID, team.getId());
						startActivity(intent);
					}else{
						startGameButton.setEnabled(false);
						Toast.makeText(PositionsActivity.this, "Не выбрано 6 игроков", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		startGameButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ArrayList<Integer> teamIDsArrayList = new ArrayList<Integer>();
				for(Player p : gamePlayersList){
					teamIDsArrayList.add(p.getId());
				}
				Intent intent = new Intent(PositionsActivity.this, GameActivity.class);
				intent.putIntegerArrayListExtra(TEAM, teamIDsArrayList);
				startActivity(intent);
			}
		});
		
		
		teamPlayersList = getHelper().getPlayers(team);

		positionsAdapter = new PositionsAdapter(this);

		playersArray = new String[teamPlayersList.size()];
		int i = 0;
		for (Player p : teamPlayersList) {
			playersArray[i] = p.getName();
			i++;
		}
		
		playersAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice,
				playersArray);

		playersListView.setAdapter(playersAdapter);

		playersListView.setItemsCanFocus(false);
		playersListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		//Collections.sort(teamPlayersList);
	}

	private class PositionsAdapter extends BaseAdapter {
		Context context;
		AlertDialog.Builder builder;
		AlertDialog dialog;
		String[] positions = {"1","2","3","4","5","6"};

		PositionsAdapter(Context c) {
			super();
			context = c;
			builder = new AlertDialog.Builder(context);
		}

		public int getCount() {
			return 6;
		}

		public Object getItem(int position) {
			return gamePlayersList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			/*final Player player = gamePlayersList.get(position);
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.position_view,
						null);
			}
			TextView nameView = (TextView) convertView.findViewById(R.id.name);
			Button button = (Button) convertView.findViewById(R.id.button);

			button.setText(player.getPosition().equals(0)?"Choose":player.getPosition().toString());
			nameView.setText(player.getName());
			
			button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					builder.setTitle("Position for " + player.getName());
					builder.setSingleChoiceItems(
							positions, 
							player.getPosition()-1, 
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							setPlayerPosition(player, which+1);
							Collections.sort(gamePlayersList);
							PositionsAdapter.this.notifyDataSetChanged();
							dialog.dismiss();
						}
					});
					dialog = builder.create();
					dialog.show();
				}
			});*/
			
			return convertView;
		}

		private void setPlayerPosition(Player player, int position){
			/*player.setPosition(position);
			boolean isTeamReady = true;
			for(Player p : gamePlayersList){
				if (p.getPosition().equals(0))
					isTeamReady = false;
				if (p.equals(player))
					continue;
				if (p.getPosition().equals(player.getPosition()))
					p.setPosition(0);
			}*/
			startGameButton.setEnabled(isTeamReady);
		}
	}

}
