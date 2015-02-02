package com.oleg.volleystat.activity;

import java.util.List;


import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.oleg.volleystat.R;
import com.oleg.volleystat.db.DatabaseHelper;
import com.oleg.volleystat.Player;
import com.oleg.volleystat.Team;

/*
import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
*/
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VolleyStatActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	private static final Object PLAYER_NAME_COLUMN = "1";
	private static final Object PLAYER_ID_COLUMN = "2";
	protected static final int CHOOSE_TEAM_DIALOG = 0;
	public static final String TEAM_ID = "teamId";
	List<Player> players;
    //DBAdapter dbAdapter;
	
	String name = "kostyuk.oleg@gmail.com";
	String pass = "bvuj46i8";
	String appName  = "oleg-VolleyStat-v1";
/*
	SpreadsheetService service;
	List<SpreadsheetEntry> spreadsheetsList;
	SpreadsheetFeed spreadsheetFeed;
	CellFeed cellFeed;
	URL cellFeedUrl;
	URL cellEntryUrl;
	SpreadsheetEntry volleyStatSpreadsheet;
	WorksheetEntry worksheetEntry;
	CellEntry cellEntry;
	FeedURLFactory factory;*/

	
	AlertDialog.Builder builder;

	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);

        
        builder = new AlertDialog.Builder(this);
        
        //LinearLayout main = new LinearLayout(this);
        //main.setOrientation(LinearLayout.VERTICAL);
        Button newGameButton = (Button) findViewById(R.id.gameButton);
        Button playersButton = (Button) findViewById(R.id.playersButton);
        Button statButton = (Button) findViewById(R.id.statButton);

        //setContentView(main);
        //main.addView(newGameButton);
        //main.addView(playersButton);

        Button b = (Button) findViewById(R.id.exportButton);
        //main.addView(b);
        b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				getHelper().exportDB();
			}
		});
        
        
        newGameButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(CHOOSE_TEAM_DIALOG);
				//Intent intent = new Intent(VolleyStatActivity.this, PositionsActivity.class);
				//VolleyStatActivity.this.startActivity(intent);
			}
		});
        
        playersButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(VolleyStatActivity.this, TeamActivity.class);
				//intent.setClass(VolleyStatActivity.this, SimpleListActivity.class);
				
				
				VolleyStatActivity.this.startActivity(intent);
			}
		});
        
        statButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(VolleyStatActivity.this, StatActivity.class);
				VolleyStatActivity.this.startActivity(intent);
			}
		});
        
/*
		service = new SpreadsheetService(appName);
		factory = FeedURLFactory.getDefault();
	    try {
			service.setUserCredentials(name, pass);
		    spreadsheetsList = service.getFeed(factory.getSpreadsheetsFeedUrl(),SpreadsheetFeed.class).getEntries();
			volleyStatSpreadsheet = spreadsheetsList.get(0);
	        boolean res = uploadStats();
	        if (res)
	        	updatePlayers();
		} catch (AuthenticationException e) {
			showDialog("Error", e.getMessage());
		} catch (IOException e) {
			showDialog("Error", e.getMessage());
		} catch (ServiceException e) {
			showDialog("Error", e.getMessage());
		}
*/


    }

/*    
	private void updatePlayers() throws IOException, ServiceException {
		List<Player> remotePlayers = loadPlayers();
		List<Player> localPlayers = dbAdapter.getPlayersList();
		List<Player> newLocalPlayers = new LinkedList<Player>();
		List<Player> newRemotePlayers = new LinkedList<Player>();
		for(Player p : localPlayers){
			if (!remotePlayers.contains(p))
				newLocalPlayers.add(p);
		}
		for(Player p : remotePlayers){
			if (!localPlayers.contains(p))
				newRemotePlayers.add(p);
		}
		uploadNewPlayers(newLocalPlayers);
		
	    dbAdapter.open();
	    for(Player p : newRemotePlayers)
		    dbAdapter.addPlayer(p);
	    dbAdapter.close();
	}

	private List<Player> loadPlayers() throws IOException, ServiceException {
	    List<Player> remotePlayers = new LinkedList<Player>();
	    WorksheetEntry playersWorksheet = volleyStatSpreadsheet.getWorksheets().get(0);
	    
	    CellQuery query = new CellQuery(playersWorksheet.getCellFeedUrl());
	    query.setMinimumRow(2);
	    query.setMaximumRow(30);
	    query.setMinimumCol(0);
	    query.setMaximumCol(5);
	    List <CellEntry> playerCellsList = service.query(query, CellFeed.class).getEntries();
	    //List <CellEntry> playerCellsList = service.getFeed(playersWorksheet.getCellFeedUrl(), CellFeed.class).getEntries();
	    
	    for (CellEntry entry : playerCellsList) {
	        String cellcol = entry.getId().substring(
	        		entry.getId().lastIndexOf('C') + 1);
	        String cellrow = entry.getId().substring(
	        		entry.getId().lastIndexOf('R') + 1,
	        		entry.getId().lastIndexOf('C'));
	        String value = entry.getPlainTextContent();
	        Player player = new Player();
	        player.setName(cellrow);
	        
	        if(cellcol.equals(PLAYER_ID_COLUMN)){
	        	Player p = new Player();
	        	p.setId(Integer.parseInt(value));
	        	remotePlayers.add(p);
	        }else if (cellcol.equals(PLAYER_NAME_COLUMN)){
	        	remotePlayers.get(Integer.parseInt(cellrow)).setName(value);
	        }
	    }
		return remotePlayers;
	}

	private void uploadNewPlayers(List<Player> newPlayers) {
		// TODO Auto-generated method stub
		
	}
*/

    private boolean uploadStats() {
		
		return false;
	}
	
	private void showDialog(String title, String text){
		builder.setTitle(title).setMessage(text).create().show();
	}

	@Override
	protected Dialog onCreateDialog(int dialogId) {
		
		switch(dialogId){
		case(CHOOSE_TEAM_DIALOG):
			final List<Team> teams = getHelper().getTeams();
			CharSequence[] items = new CharSequence[teams.size()];
			for (int i=0; i<teams.size(); i++){
				items[i] = teams.get(i).getName();
			}
			builder.setTitle(R.string.team).setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (which < 0){
						return;
					}
					int teamId = teams.get(which).getId();
					Intent intent = new Intent(VolleyStatActivity.this, PositionsActivity.class);
					intent.putExtra(TEAM_ID, teamId);
					VolleyStatActivity.this.startActivity(intent);
				}
			}).create().show();
			break;
		default:
			break;
		}
		return super.onCreateDialog(dialogId);
	}

    

}