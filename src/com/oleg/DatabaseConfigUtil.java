package com.oleg;

import java.io.IOException;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

public class DatabaseConfigUtil extends OrmLiteConfigUtil{
	
	/*private static final Class<?>[] classes = new Class[] {
	    Team.class
	  };*/
	
	public static void main(String[] args) throws IOException, java.sql.SQLException {
		writeConfigFile("ormlite_config.txt");
	}
}
