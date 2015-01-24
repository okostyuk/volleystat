package com.oleg.activity;

import java.util.LinkedList;
import java.util.List;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.oleg.Player;
import com.oleg.PlayerStat;
import com.oleg.R;
import com.oleg.db.DatabaseHelper;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

public class StatActivity extends OrmLiteBaseActivity<DatabaseHelper> implements OnCheckedChangeListener{
	
	public static final int GOAL = 0;
	public static final int FAIL = 2;
	public static final int INGAME = 1;
	public static final int ERROR = 3;
	

	ListView list;
	StatsAdapter adapter;
	List<PlayerStat> stats = new LinkedList<PlayerStat>();
	CheckBox attackCheckBox;
	CheckBox blockCheckBox;
	CheckBox putCheckBox;
	Button filterButton;
	
	boolean isAttack = true;
	boolean isBlock = true;
	boolean isPut = true;
	
	Integer teamId;
	Integer gameId;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_view);

        attackCheckBox = (CheckBox) findViewById(R.id.attackCheckbox);
        blockCheckBox = (CheckBox) findViewById(R.id.blockCheckbox);
        putCheckBox = (CheckBox) findViewById(R.id.putCheckbox);
        filterButton = (Button) findViewById(R.id.filterButton);
        
        attackCheckBox.setChecked(true);
        blockCheckBox.setChecked(true);
        putCheckBox.setChecked(true);
        
        adapter = new StatsAdapter(this);
        //stats = //new ArrayList<PlayerStat>();//Collections.EMPTY_LIST;
        //stats.add(new PlayerStat(new Player(), this));
        //PlayerStat.getStats(this);
        list = (ListView) findViewById(R.id.List);
        list.setAdapter(adapter);
        
        
        attackCheckBox.setOnCheckedChangeListener(this);
        blockCheckBox.setOnCheckedChangeListener(this);
        putCheckBox.setOnCheckedChangeListener(this);
        
        filterButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), StatFilterActivity.class); 
				startActivityForResult(intent, 0);
				//Intent intent = new Intent(getApplicationContext(), FilterActivity.class); 
				//startActivityForResult(intent, 0);
			}
		});
        
    }
    
    @Override
    protected void onResume() {
    	stats.clear();
    	stats.addAll(PlayerStat.getStats(getHelper(), teamId, gameId));
    	
    	runOnUiThread(new Runnable() {
			public void run() {
				adapter.notifyDataSetChanged();
			}
		});
    	super.onResume();
    }
    
    class StatsAdapter extends BaseAdapter{
    	Context context;
    	
    	StatsAdapter(Context c){
    		super();
    		context = c;
    	}

		public int getCount() {
			return stats.size();
		}

		public Object getItem(int arg0) {
			return stats.get(arg0);
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int pos, View convertView, ViewGroup arg2) {
			if (convertView == null){
				convertView = View.inflate(context, R.layout.stat_item_view2, null);
			}
			
			TextView name = (TextView) convertView.findViewById(R.id.name);
			TextView goalText = (TextView) convertView.findViewById(R.id.goalText);
			TextView failText = (TextView) convertView.findViewById(R.id.failText);
			TextView ingameText = (TextView) convertView.findViewById(R.id.ingameText);
			TextView errorText = (TextView) convertView.findViewById(R.id.errorText);

			PlayerStat s = stats.get(pos);
			name.setText(s.getPlayer().getName());
			int goals = (isAttack?s.getAttackStats().get(GOAL):0)
					+ (isPut?s.getPutStats().get(GOAL):0)
					+ (isBlock?s.getBlockStats().get(GOAL):0);
			int fails = (isAttack?s.getAttackStats().get(FAIL):0)
					+ (isPut?s.getPutStats().get(FAIL):0)
					+ (isBlock?s.getBlockStats().get(FAIL):0);
			int inGame = (isAttack?s.getAttackStats().get(INGAME):0)
					+ (isPut?s.getPutStats().get(INGAME):0)
					+ (isBlock?s.getBlockStats().get(INGAME):0);
			int errors = s.getErrorStats().get(0) 
					+ s.getErrorStats().get(1);
			
			goalText.setText(String.valueOf(goals));
			failText.setText(String.valueOf(fails));
			ingameText.setText(String.valueOf(inGame));
			errorText.setText(String.valueOf(errors));
			
			name.setTextColor(Color.WHITE);
			name.setTextSize(20);

			if (pos == stats.size()-1){
				convertView.setBackgroundColor(Color.GREEN);
			}else{
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}
			
			return convertView;
		}
    	
    }

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == attackCheckBox.getId()){
			isAttack = isChecked;
		}else if (buttonView.getId() == blockCheckBox.getId()){
			isBlock = isChecked;
		}else if (buttonView.getId() == putCheckBox.getId()){
			isPut = isChecked;
		}
		adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null || data.getExtras() == null){
			teamId = 0;
			gameId = 0;
			super.onActivityResult(requestCode, resultCode, data);
			return;
		}
		teamId = data.getExtras().getInt("teamId");
		gameId = data.getExtras().getInt("gameId");
		
		super.onActivityResult(requestCode, resultCode, data);
	}
}
