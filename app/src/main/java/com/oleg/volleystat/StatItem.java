package com.oleg.volleystat;

public class StatItem {
	private Long timestamp;
	private int statType;
	private int statValue;
	private long gameId;
	
	public StatItem(Long t, int type, int val, long id){
		timestamp = t;
		statType = type;
		statValue = val;
		gameId = id;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public int getStatType() {
		return statType;
	}

	public int getStatValue() {
		return statValue;
	}
	
	public long getGameId(){
		return gameId;
	}
	
	
}
