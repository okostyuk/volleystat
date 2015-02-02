package com.oleg.volleystat;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "games")
public class Game implements Item{
	static final int ID = 101;
	
	@DatabaseField(generatedId = true)
	int id;

	@DatabaseField(foreign = true)
	Team team;
	
	@DatabaseField
	String enemyName;
	
	@DatabaseField
	Date startTime;
	
	@DatabaseField
	Date endTime;
	
	@DatabaseField
	int theirScore;
	
	@DatabaseField
	int ourScore;
	
	public Team getTeam() {
		return team;
	}

	public String getEnemyName() {
		return enemyName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public int getTheirScore() {
		return theirScore;
	}

	public int getOurScore() {
		return ourScore;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public void setEnemyName(String enemyName) {
		this.enemyName = enemyName;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public void setTheirScore(int theirScore) {
		this.theirScore = theirScore;
	}

	public void setOurScore(int ourScore) {
		this.ourScore = ourScore;
	}

	public int getId() {
		return id;
	}
	
	public String toString() {
		if (team != null)
			return new SimpleDateFormat("dd.MM.yyyy HH:mm").format(startTime);
		else
			return "NO NAME";
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
	
	public void setId(int i) {
		id = i;
	}
	
}
