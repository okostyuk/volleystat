package com.oleg.volleystat;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "actions")
public class Action implements Item{
	@DatabaseField(generatedId = true)
	int id;
	
	@DatabaseField 
	Date date;
	
	@DatabaseField(dataType = DataType.ENUM_INTEGER)
	ActionType type;
	
	@DatabaseField(foreign = true) 
	Player player;
	
	@DatabaseField(foreign = true) 
	Game game;

	@DatabaseField(foreign = true) 
	Stat stat;
	
	@DatabaseField() 
	Boolean isValid = true;
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public ActionType getType() {
		return type;
	}

	public void setType(ActionType type) {
		this.type = type;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Stat getStat() {
		return stat;
	}

	public void setStat(Stat stat) {
		this.stat = stat;
	}
	
	
	boolean selected;
	
	public void setInvalid(){
		isValid = false;
	}

	public int getId() {
		return id;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean b) {
		selected = b;
	}

	public boolean isValid() {
		return isValid;
	}
	public void setValid() {
		isValid = true;
	}
	
}
