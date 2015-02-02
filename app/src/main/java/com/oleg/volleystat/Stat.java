package com.oleg.volleystat;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "stats")
public class Stat implements Item{
	@DatabaseField(generatedId = true)
	int id;
	
	@DatabaseField(foreign = true)
	Game game;
	
	@DatabaseField(foreign = true)
	Player player;
	
	@DatabaseField
	Date date;
	
	@DatabaseField(dataType = DataType.ENUM_INTEGER)
	StatType type;

	@DatabaseField(dataType = DataType.ENUM_INTEGER)
	StatValue value;

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public StatType getType() {
		return type;
	}

	public void setType(StatType type) {
		this.type = type;
	}

	public StatValue getValue() {
		return value;
	}

	public void setValue(StatValue value) {
		this.value = value;
	}
	
	public int getId(){
		return id;
	}
	
	@DatabaseField() 
	Boolean isValid = true;
	boolean selected;
	
	public void setInvalid(){
		isValid = false;
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
