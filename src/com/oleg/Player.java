package com.oleg;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "players")
public class Player implements Item{
	static final int ID = 103;

	@DatabaseField(generatedId = true)
	int id;
	
	@DatabaseField(foreign = true)
	Team team;
	
	@DatabaseField
	String name;
	
	@ForeignCollectionField(eager = false)
	ForeignCollection<Stat> stats;
	
	int putCount = 0;
	int blockCount = 0;
	int attackCount = 0;
	int errorCount = 0;

	public String getName() {
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public int getPutCount() {
		return putCount;
	}

	public int getBlockCount() {
		return blockCount;
	}

	public int getAttackCount() {
		return attackCount;
	}

	public int getErrorCount() {
		return errorCount;
	}
	
	public void incPutCount(){
		putCount++;
	}
	public void incAttackCount(){
		attackCount++;
	}
	public void incBlockCount(){
		blockCount++;
	}
	public void incErrorCount(){
		errorCount++;
	}

	public void decPutCount(){
		putCount--;
	}
	public void decAttackCount(){
		attackCount--;
	}
	public void decBlockCount(){
		blockCount--;
	}
	public void decErrorCount(){
		errorCount--;
	}

	public Team getTeam() {
		return team;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Player && ((Player) o).getId() == id)
			return true;
		return false;
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
