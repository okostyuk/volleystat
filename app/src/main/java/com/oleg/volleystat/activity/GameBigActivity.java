package com.oleg.volleystat.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.oleg.volleystat.R;
import com.oleg.volleystat.db.DatabaseHelper;

public class GameBigActivity extends OrmLiteBaseActivity<DatabaseHelper>  {
    
	GridView grid1;
	GridView grid2;
	
	TeamAdapter teamAdapter;
	//EnemyAdapter enemyAdapter;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);
        
        grid1 = (GridView) findViewById(R.id.grid1);
        grid2 = (GridView) findViewById(R.id.grid2);
        
        
    }
	
	
	class TeamAdapter extends BaseAdapter{

		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
