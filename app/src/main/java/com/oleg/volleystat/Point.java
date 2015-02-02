package com.oleg.volleystat;

public class Point {
	int id;
	int game_id;
	int scored;
	long date;
	int stats_id;
	
	public Point (int id, int game_id, int scored, long date, int stats_id){
		this.id = id;
		this.game_id = game_id;
		this.scored = scored;
		this.date = date;
		this.stats_id = stats_id;
	}

}
