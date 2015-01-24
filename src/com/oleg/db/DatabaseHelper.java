package com.oleg.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.oleg.Action;
import com.oleg.ActionType;
import com.oleg.Game;
import com.oleg.Player;
import com.oleg.PlayerStat;
import com.oleg.R;
import com.oleg.Stat;
import com.oleg.StatItem;
import com.oleg.StatType;
import com.oleg.StatValue;
import com.oleg.Team;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper{
	private static final String DATABASE_NAME = "VolleyStat.sqlite";
	private static final String DB_PATH = "/data/data/com.oleg/databases/";
	private static final int DATABASE_VERSION = 1;

	private RuntimeExceptionDao<Team, Integer> teamDao = null;
	private RuntimeExceptionDao<Player, Integer> playerDao = null;
	private RuntimeExceptionDao<Action, Integer> actionDao = null;
	private RuntimeExceptionDao<ActionType, Integer> ActionTypeDao = null;
	private RuntimeExceptionDao<Game, Integer> gameDao = null;
	private RuntimeExceptionDao<Stat, Integer> statDao = null;
	private RuntimeExceptionDao<StatType, Integer> statTypeDao = null;
	private RuntimeExceptionDao<StatValue, Integer> statValueDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, Team.class);
			TableUtils.createTable(connectionSource, Player.class);
			TableUtils.createTable(connectionSource, Action.class);
//			TableUtils.createTable(connectionSource, ActionType.class);
			TableUtils.createTable(connectionSource, Game.class);
			TableUtils.createTable(connectionSource, Stat.class);
			//TableUtils.createTable(connectionSource, StatType.class);
			//TableUtils.createTable(connectionSource, StatValue.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
		
		Team testTeam = new Team();
		
		testTeam.setName("Штиль");
		getTeamDao().create(testTeam);

		// here we try inserting data in the on-create as a test
		/*RuntimeExceptionDao<Team, Integer> dao = getTeamDao();
		long millis = System.currentTimeMillis();
		//create some entries in the onCreate
		/*Team simple = new Team(millis);
		dao.create(simple);
		simple = new Team(millis + 1);
		dao.create(simple);
		Log.i(DatabaseHelper.class.getName(), "created new entries in onCreate: " + millis);*/
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, Team.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	public RuntimeExceptionDao<Team, Integer> getTeamDao() {
		if (teamDao == null) {
			teamDao = getRuntimeExceptionDao(Team.class);
		}
		return teamDao;
	}
	
	public RuntimeExceptionDao<Player, Integer> getPlayerDao() {
		if (playerDao == null) {
			playerDao = getRuntimeExceptionDao(Player.class);
		}
		return playerDao;
	}

	public RuntimeExceptionDao<Action, Integer> getActionDao() {
		if (actionDao == null) {
			actionDao = getRuntimeExceptionDao(Action.class);
		}
		return actionDao;
	}

	public RuntimeExceptionDao<Game, Integer> getGameDao() {
		if (gameDao == null) {
			gameDao = getRuntimeExceptionDao(Game.class);
		}
		return gameDao;
	}
	
	public RuntimeExceptionDao<Stat, Integer> getStatDao() {
		if (statDao == null) {
			statDao = getRuntimeExceptionDao(Stat.class);
		}
		return statDao;
	}
	
	
	@Override
	public void close() {
		super.close();
		teamDao = null;
	}
	
	public List<Team> getTeams() {
		List<Team> list = getEmptyList();
		try{
			list.addAll(getTeamDao().queryBuilder()
					.where().eq("isValid", true)
					.query());
		}catch (Exception e) {
			return list;
		}
		return list;
	}

	public void exportDB() {
		File outFile = new File("/sdcard/" + DATABASE_NAME);
        InputStream myInput = null; 
		OutputStream myOutput = null;
        try {
    		boolean b = outFile.createNewFile();
	        myInput = new FileInputStream(DB_PATH + DATABASE_NAME);
			myOutput = new FileOutputStream(outFile);
	 
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = myInput.read(buffer))>0){
	            myOutput.write(buffer, 0, length);
	        }
	 
	        myOutput.close();
	        myInput.close();
        } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Player> getPlayers(Team team) {
		List<Player> list = getEmptyList();
		if (team == null)
			list.addAll(getPlayers());
		try{
			list.addAll(getPlayerDao().queryBuilder()
					.where().eq("team_id", team)
					.and().eq("isValid", true)
					.query());
		}catch (Exception e) {
			return list;
		}
		return list;
	}

	public List<Player> getPlayers() {
		List<Player> list = getEmptyList();
		try{
			list.addAll(getPlayerDao().queryBuilder()
					.where().eq("isValid", true)
					.query());
			return list;
		}catch (Exception e) {
			return list;
		}
	}
	
	
	public void removePlayer(Player player) {
		player.setInvalid();
		getPlayerDao().createOrUpdate(player);
	}


	public Team getTeam(Integer teamId) {
		if (teamId == null)
			return null;
		return getTeamDao().queryForId(teamId);
	}

	public List<Player> getPlayersListIn(ArrayList<Integer> playerIDsList) {
		LinkedList<Player> players = new LinkedList<Player>();
		for (Integer id : playerIDsList){
			players.add(getPlayerDao().queryForId(id));
		}
		return players;
	}


	public void createOrUpdateStat(Stat stat) {
		getStatDao().createOrUpdate(stat);
	}
	
	public void createOrUpdateAction(Action action) {
		getActionDao().createOrUpdate(action);
	}
	
	public void createOrUpdatePlayer(Player player) {
		getPlayerDao().createOrUpdate(player);
	}

	public void createOrUpdateTeam(Team team) {
		getTeamDao().createOrUpdate(team);
	}

	public void createOrUpdateGame(Game game) {
		getGameDao().createOrUpdate(game);
	}

	
	public Action getLastAction(Game game, ActionType type){
		try {
			List<Action> actions = getActionDao().queryBuilder()
			.orderBy("id", true)
			.where().eq("game_id", game.getId())
			.and().eq("type", type)
			.and().eq("isValid", true)
			.query();
			if (actions == null || actions.isEmpty())
				return null;
			Action action = actions.get(actions.size()-1);

			getStatDao().refresh(action.getStat());
			/*
			if (action.getStat() != null){
				Stat s = getStatDao().queryForId(action.getStat().getId());
				action.setStat(s);
			}*/
			return action;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Stat> getStatItems(Player p) {
		List<Stat> list = getEmptyList();
		try {
			list.addAll(getStatDao().queryBuilder()
					.where().eq("player_id", p.getId())
					.and().eq("isValid", true)
					.query());
		}catch (Exception e) {
			return list;
		}
		return list;
	}

	public Game getGame(Integer gameId) {
		if (gameId == null)
			return null;
		return getGameDao().queryForId(gameId);
	}

	public List<Stat> getStatItems(Player p, Game game) {
		List<Stat> list = getEmptyList();
		try {
			if (game == null)
				list.addAll(getStatDao().queryBuilder()
						.where().eq("player_id", p.getId())
						.and().eq("isValid", true)
						.query());
			else
				list.addAll(getStatDao().queryBuilder()
						.where().eq("player_id", p.getId())
						.and().eq("game_id", game.getId())
						.and().eq("isValid", true)
						.query());
		}catch (Exception e) {
			return list;
		}
		return list;
	}

	public List<Game> getGames() {
		List<Game> list = getEmptyList();
		list.addAll(getGameDao().queryForAll());
		return list;
	}
	
	public List<Game> getGames(Team team) {
		List<Game> list = getEmptyList();
		list.addAll(getGameDao().queryForAll());
		if (team == null){
			list.addAll(getGames());
			return list;
		}
		try {
			list.addAll(getGameDao().queryBuilder()
					.where().eq("team_id", team.getId())
					.and().eq("isValid", true)
					.query());
		} catch (SQLException e) {
			return list;
		}
		return list;
	}

	public Player getPlayer(Integer playerId) {
		if (playerId == null)
			return null;
		return getPlayerDao().queryForId(playerId);
	}
	
	public <T> List<T> getItems(Class<T> clazz){
		if (Team.class.equals(clazz)){
			return (List<T>) getTeams();
		}
		if (Game.class.equals(clazz)){
			return (List<T>) getGames();
		}
		if (Player.class.equals(clazz)){
			return (List<T>) getPlayers();
		}
		return getEmptyList();
	}
	
	private <T> List<T> getEmptyList(){
		return new ArrayList<T>();
	}
	
}
