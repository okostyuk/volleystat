package com.oleg.volleystat;

import com.j256.ormlite.table.DatabaseTable;

//@DatabaseTable(tableName = "statValues")
public enum StatValue {
	WIN,
	LOOSE,
	INGAME,
	ERROR_GRID,
	ERROR_SPADE
}
