package com.oleg;

import java.util.ArrayList;
import java.util.List;

import com.oleg.db.DatabaseHelper;

public class PlayerStat {
	private final Player player;
	
	private final ArrayList<Integer> putStats = new ArrayList<Integer>();
	private final ArrayList<Integer> attackStats = new ArrayList<Integer>();
	private final ArrayList<Integer> blockStats = new ArrayList<Integer>();
	private final ArrayList<Integer> errorStats = new ArrayList<Integer>();
	private final ArrayList<Stat> items = new ArrayList<Stat>();
	
	int goals; 
	int fails;
	int inGame;
	int errors;
	
	
	public static ArrayList<PlayerStat> getStats(DatabaseHelper databaseHelper, Integer teamId, Integer gameId){
		ArrayList<PlayerStat> stats = new ArrayList<PlayerStat>();
		Team team = databaseHelper.getTeam(teamId);
		Game game = databaseHelper.getGame(gameId);
		List<Player> players = databaseHelper.getPlayers(team);//dbAdapter.getPlayersList();
		Player totalPlayer = new Player();
		totalPlayer.setName("Всего");
		PlayerStat totalStat = new PlayerStat(totalPlayer);
		for (Player p : players){
			PlayerStat stat = new PlayerStat(p);
			List<Stat> statItems = databaseHelper.getStatItems(p, game);
			for (Stat item : statItems){
				stat.addStatLocal(item);
				totalStat.addStatLocal(item);
						//item.getType(), item.getValue(), item.getGame().getId());
			}
			stats.add(stat);
		}
		stats.add(totalStat);
		return stats;
	}


	public PlayerStat(Player player){
		this.player = player;
/*		putStats.set(StatValue.WIN.ordinal(), 0);
		putStats.set(StatValue.LOOSE.ordinal(), 0);
		putStats.set(StatValue.INGAME.ordinal(), 0);
		attackStats.set(StatValue.WIN.ordinal(), 0);
		attackStats.set(StatValue.LOOSE.ordinal(), 0);
		attackStats.set(StatValue.INGAME.ordinal(), 0);
		blockStats.set(StatValue.WIN.ordinal(), 0);
		blockStats.set(StatValue.LOOSE.ordinal(), 0);
		blockStats.set(StatValue.INGAME.ordinal(), 0);
		errorStats.set(StatValue.ERROR_GRID.ordinal(), 0);
		errorStats.set(StatValue.ERROR_SPADE.ordinal(), 0);*/
		
		
		putStats.add(0);putStats.add(0);putStats.add(0);putStats.add(0);
		attackStats.add(0);attackStats.add(0);attackStats.add(0);attackStats.add(0);
		blockStats.add(0);blockStats.add(0);blockStats.add(0);blockStats.add(0);
		errorStats.add(0);errorStats.add(0);

		goals = getAttackStats().get(StatValue.WIN.ordinal())
				+ getPutStats().get(StatValue.WIN.ordinal())
				+ getBlockStats().get(StatValue.WIN.ordinal());
		fails = getAttackStats().get(StatValue.LOOSE.ordinal())
				+ getPutStats().get(StatValue.LOOSE.ordinal())
				+ getBlockStats().get(StatValue.LOOSE.ordinal());
		inGame = getAttackStats().get(StatValue.INGAME.ordinal())
				+ getPutStats().get(StatValue.INGAME.ordinal())
				+ getBlockStats().get(StatValue.INGAME.ordinal());
		errors = getErrorStats().get(0) 
				+ getErrorStats().get(1);
	}
	
	
	public ArrayList<Integer> getPutStats() {
		return putStats;
	}
	public ArrayList<Integer> getAttackStats() {
		return attackStats;
	}
	public ArrayList<Integer> getBlockStats() {
		return blockStats;
	}
	public ArrayList<Integer> getErrorStats() {
		return errorStats;
	}
	
	//private void addStatLocal(StatType type, StatValue value, long id) {
	private void addStatLocal(Stat stat) {
		StatValue value = stat.getValue();
		items.add(stat);
				//new StatItem(System.currentTimeMillis(), type, value, gameId));
		switch(stat.getType()){
		case PUT:
			putStats.set(value.ordinal(), putStats.get(value.ordinal())+1);
			if (value.equals(StatValue.WIN))
				goals++;
			else if (value.equals(StatValue.INGAME))
				inGame++;
			else if (value.equals(StatValue.LOOSE))
				fails++;
			break;
		case ATTACK:
			attackStats.set(value.ordinal(), attackStats.get(value.ordinal())+1);
			if (value.equals(StatValue.WIN))
				goals++;
			else if (value.equals(StatValue.INGAME))
				inGame++;
			else if (value.equals(StatValue.LOOSE))
				fails++;
			break;
		case BLOCK:
			blockStats.set(value.ordinal(), blockStats.get(value.ordinal())+1);
			if (value.equals(StatValue.WIN))
				goals++;
			else if (value.equals(StatValue.INGAME))
				inGame++;
			else if (value.equals(StatValue.LOOSE))
				fails++;
			break;
		case ERROR:
			if (value.equals(StatValue.ERROR_GRID))
				errorStats.set(0, errorStats.get(0)+1);
			else if (value.equals(StatValue.ERROR_SPADE))
				errorStats.set(1, errorStats.get(1)+1);
			errors++;
			break;
		}
		
	}


/*	public long addStat(int type, int value, long gameId) {
		dbAdapter.open();
		long statId = dbAdapter.addStat(gameId, player.getId(), type, value);
		dbAdapter.close();
		addStatLocal(type, value, gameId);
		return statId;
	}*/


	public Player getPlayer() {
		return player;
	}
	
}
