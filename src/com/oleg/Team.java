package com.oleg;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "teams")
public class Team implements Item{
	static final int ID = 102;

	private boolean selected = false;
	
	@DatabaseField(generatedId = true)
	int id;
	
	@DatabaseField
	String name;
	
	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		if (name != null)
			return name;
		else
			return "Все";
	}

	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean b){
		selected = b;
	}
	
	@Override
	public boolean equals(Object o) {
		if (super.equals(o))
			return true;
		else
			return (Team.class.isInstance(o) && ((Team) o).getId() == id);
	}
	
	@DatabaseField() 
	Boolean isValid = true;
	
	public void setInvalid(){
		isValid = false;
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
