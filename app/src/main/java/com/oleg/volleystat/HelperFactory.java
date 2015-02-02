package com.oleg.volleystat;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.oleg.volleystat.db.DatabaseHelper;

/**
 * Created by Oleg on 02.02.2015.
 */
public class HelperFactory {
    private static DatabaseHelper databaseHelper;

    public static void setHelper(Context context){
        databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }
    public static void releaseHelper(){
        OpenHelperManager.releaseHelper();
        databaseHelper = null;
    }
}
