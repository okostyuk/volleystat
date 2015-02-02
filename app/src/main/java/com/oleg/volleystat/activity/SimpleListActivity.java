package com.oleg.volleystat.activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.oleg.volleystat.Game;
import com.oleg.volleystat.Item;
import com.oleg.volleystat.Items;
import com.oleg.volleystat.Player;
import com.oleg.volleystat.R;
import com.oleg.volleystat.Team;
import com.oleg.volleystat.db.DatabaseHelper;

public class SimpleListActivity extends OrmLiteBaseActivity<DatabaseHelper>{

	int id;
	Items type;
	Class editItemActivityClass;
	Class itemClass;
	List<Item> items;
	Item itemAll;
	int selectedItem;
	BaseAdapter adapter;
	ListView listView;
	Button okButton;
	Button cancelButton;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_list_view);
        listView = (ListView) findViewById(R.id.listView);
        okButton = (Button) findViewById(R.id.okButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        id = getIntent().getIntExtra("id", -1);
        type = (Items) getIntent().getSerializableExtra("type");

        switch (type){
        case TEAM:
        	itemClass = Team.class;
        	Team tmp = new Team();
        	tmp.setId(-1);
        	itemAll = tmp;
        	//editItemActivityClass = TODO
        	break;
        }

        items = getHelper().getItems(itemClass);
        items.add(itemAll);
        
    	if (id == -1){
    		selectedItem = items.size()-1;
    	}else{
    		Team team = getHelper().getTeam(id);
    		selectedItem = items.indexOf(team);
    		items.get(selectedItem).setSelected(true);
    	}
    	
    	Log.d("start", "selectedItem=" + selectedItem);
    	
    	adapter = setAdapter(items);
        

        listView.setAdapter(adapter);
        
        okButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("id", id);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
        
        listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
		    	id = items.get(pos).getId();
			}
		});
    }	
    
    private <T extends Item> 
    ItemAdapter<T> 
    setAdapter(List<T> items){
    	ItemAdapter<T > adapter
    	= new ItemAdapter<T>(this, 
    			android.R.layout.simple_list_item_checked, 
    			//android.R.layout.simple_list_item_single_choice,
    			android.R.id.text1, 
    			items
    			){
    	};
    	return adapter;
    }
    
    class ItemAdapter<T extends Item> extends ArrayAdapter<T>{
		public ItemAdapter(Context context, int resource,
				int textViewResourceId, List<T> objects) {
			super(context, resource, textViewResourceId, objects);
		}
		
		@Override
		public long getItemId(int position) {
			return getItem(position).getId();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CheckedTextView view = (CheckedTextView) super.getView(position, convertView, parent);
			view.setChecked(items.get(position).isSelected());
			return view;
		}
    	
    }
	
}
