package com.oleg.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.oleg.R;
import com.oleg.db.DatabaseHelper;
import com.oleg.ActionType;
import com.oleg.Game;
import com.oleg.Player;
import com.oleg.Stat;
import com.oleg.Action;
import com.oleg.StatType;
import com.oleg.StatValue;
import com.oleg.Team;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class GameActivity extends OrmLiteBaseActivity<DatabaseHelper>  {
	ListView playersList;
	PlayersAdapter playersAdapter;
	List<Player> teamPlayers;
	Game game;
	Player selectedPlayer;
	
	Dialog endGameDialog;
	Button endGameDialogCancel;
	Button endGameDialogFinish;
	EditText opponentName;
	EditText theirScore;
	EditText ourScore;
	
	TextView teamName;
	TextView enemyTeamName;
	Button ourScoreButton;
	Button theirScoreButton;
	TextView ourScoreText;
	TextView theirScoreText;
	
	static final int REPLACE = 0;
	List<Player> outPlayers;
	String[] outPlayerNamesArray;
	Team team;
	int ourScoreValue;
	int theirScoreValue;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

		team = getHelper().getTeam(getIntent().getExtras().getInt(VolleyStatActivity.TEAM_ID));
    	ArrayList<Integer> playerIDsList = getIntent().getExtras().getIntegerArrayList(PositionsActivity.TEAM);
    	teamPlayers = getHelper().getPlayersListIn(playerIDsList);
    	outPlayers = getHelper().getPlayers(team);

		game = new Game();
        game.setStartTime(new Date());
        game.setTeam(team);
        game.setInvalid();
		getHelper().createOrUpdateGame(game);
        
        teamName = (TextView) findViewById(R.id.teamName);
        enemyTeamName = (TextView) findViewById(R.id.enemyName);
        ourScoreButton = (Button) findViewById(R.id.ourScoreButton);
        theirScoreButton= (Button) findViewById(R.id.theirScoreButton);

        ourScoreText = (TextView) findViewById(R.id.ourScoreText);
        theirScoreText = (TextView) findViewById(R.id.theirScoreText);
        
        
		ourScoreValue = 0;
		theirScoreValue = 0;
    	playersAdapter = new PlayersAdapter(this);
		teamName.setText(team.getName());
		ourScoreText.setText(String.valueOf(ourScoreValue));
		theirScoreText.setText(String.valueOf(theirScoreValue));
    	
		outPlayers.removeAll(teamPlayers);
    	
    	outPlayerNamesArray = new String[outPlayers.size()];
    	for (int i=0; i< outPlayers.size(); i++){
    		outPlayerNamesArray[i] = outPlayers.get(i).getName();
    	}
		

    	
        playersList = (ListView) findViewById(R.id.playersList);
        playersList.setAdapter(playersAdapter);
        
        playersList.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> adapter, View view, int index, long id) {
				selectedPlayer = teamPlayers.get(index);
	    		final AlertDialog.Builder builder = new  AlertDialog.Builder(GameActivity.this);
	    		builder.setTitle("Замена " + selectedPlayer.getName() + " на:");
	    		builder.setItems(outPlayerNamesArray, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Player leftPlayer = selectedPlayer;
						Player comePlayer = outPlayers.get(which);
						
						int leftPlayerIndex = teamPlayers.indexOf(leftPlayer);
						int comePlayerIndex = outPlayers.indexOf(comePlayer);
						
						teamPlayers.remove(leftPlayerIndex);
						teamPlayers.add(leftPlayerIndex, comePlayer);
						
						outPlayers.remove(comePlayer);
						outPlayers.add(comePlayerIndex, leftPlayer);
						outPlayerNamesArray[which] = leftPlayer.getName();
						
			    		runOnUiThread(new Runnable(){
							public void run() {
								playersAdapter.notifyDataSetChanged();
							}
			    		});
					}
				});
	    		
	    		runOnUiThread(new Runnable(){
					public void run() {
						builder.create().show();
					}
	    		});
				return true;
			}
		});

    	endGameDialog = new Dialog(this);
    	endGameDialog.setContentView(R.layout.end_game_dialog);
    	endGameDialog.setTitle("Игра закончилась ?");
    	
    	theirScore = (EditText) endGameDialog.findViewById(R.id.theirScore);
    	ourScore = (EditText) endGameDialog.findViewById(R.id.ourScore);
    	opponentName = (EditText) endGameDialog.findViewById(R.id.opponentName);
    	TextView dialogTeamName = (TextView) endGameDialog.findViewById(R.id.teamNameText);
    	dialogTeamName.setText(team.getName());
    	
    	endGameDialogCancel = (Button) endGameDialog.findViewById(R.id.cancelButton);
    	endGameDialogFinish = (Button) endGameDialog.findViewById(R.id.yesButton);
    	
    	endGameDialogCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				endGameDialog.dismiss();
			}
		});
    	
    	endGameDialogFinish.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				game.setEndTime(new Date());
				game.setOurScore(Integer.parseInt(ourScore.getText().toString()));
				game.setTheirScore(Integer.parseInt(theirScore.getText().toString()));
				game.setEnemyName(opponentName.getText().toString());
				game.setValid();
				getHelper().createOrUpdateGame(game);
				
				endGameDialog.dismiss();
				GameActivity.super.finish();
			}
		});

        
        ourScoreButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				addAction(ActionType.GOAL, null, null);
			}
		});

        theirScoreButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				addAction(ActionType.FAIL, null, null);
			}
		});
        
        ourScoreButton.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				undoAction(getHelper().getLastAction(game, ActionType.GOAL));
				return true;
			}
		});

        theirScoreButton.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				undoAction(getHelper().getLastAction(game, ActionType.FAIL));
				return true;
			}
		});
        
        
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        	endGameDialog.show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch (id){
    	case (REPLACE):
    		AlertDialog.Builder builder = new  AlertDialog.Builder(this);
    		builder.setTitle("Замена");
    		builder.setItems(outPlayerNamesArray, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Player leftPlayer = selectedPlayer;
					Player comePlayer = outPlayers.get(which);
					
					int leftPlayerIndex = teamPlayers.indexOf(leftPlayer);
					int comePlayerIndex = outPlayers.indexOf(comePlayer);
					
					teamPlayers.remove(leftPlayerIndex);
					teamPlayers.add(leftPlayerIndex, comePlayer);
					
					outPlayers.remove(comePlayer);
					outPlayers.add(comePlayerIndex, leftPlayer);
					outPlayerNamesArray[which] = leftPlayer.getName();
					
					playersAdapter.notifyDataSetChanged();
				}
			});
    		
    		return builder.create();
    	default:
        	return super.onCreateDialog(id);
    	}
    }
   
    
	private void addStat(StatType statType, StatValue statValue, Player player){
		Stat stat = new Stat();
		stat.setDate(new Date());
		stat.setType(statType);
		stat.setValue(statValue);
		stat.setGame(game);
		stat.setPlayer(player);
		getHelper().createOrUpdateStat(stat);
		
		if (statValue == StatValue.WIN){
			addAction(ActionType.GOAL, player, stat);
		}else if (statValue == StatValue.LOOSE || statType == StatType.ERROR){
			addAction(ActionType.FAIL, player, stat);
		}
	}

	private void addAction(ActionType type, Player player, Stat stat) {
		Action action = new Action();
		action.setDate(new Date());
		action.setGame(game);
		action.setPlayer(player);
		action.setType(type);
		action.setStat(stat);
		getHelper().createOrUpdateAction(action);
		
		if (type == ActionType.FAIL){
			theirScoreValue++;
			theirScoreText.setText(String.valueOf(theirScoreValue));
		}else if (type == ActionType.GOAL){
			ourScoreValue++;
			ourScoreText.setText(String.valueOf(ourScoreValue));
		}
		
		theirScore.setText(String.valueOf(theirScoreValue));
		ourScore.setText(String.valueOf(ourScoreValue));
		
    	theirScoreText.setText(String.valueOf(theirScoreValue));
    	ourScoreText.setText(String.valueOf(ourScoreValue));

    	if (ourScoreValue > 24 || theirScoreValue > 24){
    		endGameDialog.show();
    	}
	}

	private void undoAction(Action action){
		if (action == null)
			return;
		Stat stat = action.getStat();
		if (action.getType() == ActionType.FAIL){
			theirScoreValue--;
			theirScoreText.setText(String.valueOf(theirScoreValue));
		}else if (action.getType() == ActionType.GOAL){
			ourScoreValue--;
			ourScoreText.setText(String.valueOf(ourScoreValue));
		}
		
		action.setInvalid();
		getHelper().createOrUpdateAction(action);
		
		if (stat != null){
			int playerIndex = teamPlayers.indexOf(action.getPlayer());
			if (playerIndex < 0)
				return;
			Player player = teamPlayers.get(playerIndex);
			switch(stat.getType()){
			case ATTACK:
				player.decAttackCount();
				break;
			case BLOCK:
				player.decBlockCount();
				break;
			case PUT:
				player.decPutCount();
				break;
			case ERROR:
				player.decErrorCount();
				break;
			}
			playersAdapter.notifyDataSetChanged();
			stat.setInvalid();
			getHelper().createOrUpdateStat(stat);
		}
	}
	
	private StatValue intToStatValue(StatType type, int value){
		StatValue statValue = null;
		if (type == StatType.ATTACK || type == StatType.BLOCK || type == StatType.PUT){
			if (value == 0)
				statValue = StatValue.WIN;
			else if (value == 1)
				statValue = StatValue.INGAME;
			else if (value == 2)
				statValue = StatValue.LOOSE;
		}else if (type == StatType.ERROR){
			if (value == 0)
				statValue = StatValue.ERROR_GRID;
			else if (value == 1)
				statValue = StatValue.ERROR_SPADE;
		}
		return statValue;
	}

    
    
    private class PlayersAdapter extends BaseAdapter{
    	final CharSequence[] putItems = {"Очко", "Мяч в игре", "Аут"};
    	final CharSequence[] attackItems = {"Очко", "Мяч в игре", "Аут"};
    	final CharSequence[] blockItems = {"Очко", "Мяч в игре", "Аут"};
    	final CharSequence[] errorItems = {"Сетка", "Заступ"};
    	
    	final AlertDialog.Builder builder;
    	
    	GameActivity context;

    	PlayersAdapter(GameActivity context){
    		super();
    		this.context = context;
    		builder = new AlertDialog.Builder(context);
    	}
    	
		public int getCount() {
			return teamPlayers.size();
		}

		public Object getItem(int position) {
			return teamPlayers.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			final Player player = teamPlayers.get(position);
			final int putCount = player.getPutCount();
			final int blockCount = player.getBlockCount();
			final int attackCount = player.getAttackCount();
			final int errorCount = player.getErrorCount();
			
			if (convertView == null){
				convertView = View.inflate(context, R.layout.player_item, null);
			}
			
			//convertView.setBackgroundColor(android.R.color.transparent);
			
			TextView playerName = (TextView) convertView.findViewById(R.id.playerName);
			playerName.setText(player.getName());
			playerName.setLongClickable(true);
			//playerName.setBackgroundColor(android.R.color.transparent);
			
			/*
			playerName.setOnLongClickListener(new OnLongClickListener() {
				public boolean onLongClick(View v) {
					selectedPlayer = player;
					showDialog(REPLACE);
					return true;
				}
			});*/
			
			Button putButton = (Button) convertView.findViewById(R.id.putButton);
			putButton.setText(String.valueOf(putCount));
			putButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					builder.setTitle("Подача " + player.getName());
					builder.setItems(putItems, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, final int which) {
							StatValue statValue = intToStatValue(StatType.PUT, which);
							addStat(StatType.PUT, statValue, player);
							player.incPutCount();
							notifyDataSetChanged();
						}
					});
					builder.create().show();
				}
			});
			
			Button attackButton = (Button) convertView.findViewById(R.id.attackButton);
			attackButton.setText(String.valueOf(attackCount));
			attackButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					builder.setTitle("Атака " + player.getName());
					builder.setItems(attackItems, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							StatValue statValue = intToStatValue(StatType.ATTACK, which);
							addStat(StatType.ATTACK, statValue, player);
							player.incAttackCount();
							notifyDataSetChanged();
						}
					});
					builder.create().show();
				}
			});
			
			Button blockButton = (Button) convertView.findViewById(R.id.blockButton);
			blockButton.setText(String.valueOf(blockCount));
			blockButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					builder
						.setTitle("Блок " + player.getName())
						.setItems(blockItems, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								StatValue statValue = intToStatValue(StatType.BLOCK, which);
								addStat(StatType.BLOCK, statValue, player);
								player.incBlockCount();
								notifyDataSetChanged();
							}
						})
						.create()
						.show();
				}
			});

			Button errorButton = (Button) convertView.findViewById(R.id.errorButton);
			errorButton.setText(String.valueOf(errorCount));
			errorButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					builder.setTitle("Ошибка " + player.getName());
					builder.setItems(errorItems, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							StatValue statValue = intToStatValue(StatType.ERROR, which);
							addStat(StatType.ERROR, statValue, player);
							player.incErrorCount();
							notifyDataSetChanged();
						}
					});
					builder.create().show();
				}
			});

			
			
			return convertView;
		}
    	
    }
}
