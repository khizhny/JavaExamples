package com.khizhny.ukrsibbanking;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class RuleListActivity extends AppCompatActivity{
	private ListView listView;
	private List<Rule> ruleList;
	private RuleListAdapter adapter;
	int selected_row;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bank_list);
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {              
            	// if some rule clicked open popup window with options Add,Edit,Delete.
            	selected_row=position;
            	Rule rule = (Rule) listView.getItemAtPosition(position);
	    		/*DataBaseHelper myDb = new DataBaseHelper(RuleListActivity.this);
	        	myDb.openDataBase();
	        	myDb.setActiveBank(rule.getId());
	        	myDb.close();
	        	ruleList.clear();
	        	ruleList.addAll(myDb.getAllBanks());
	        	adapter.notifyDataSetChanged();/**/
            }
       }); 
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        ruleList=new ArrayList<Rule>();
    	DataBaseHelper db = new DataBaseHelper(this);
	 	try {	 
	 		db.openDataBase();
	 	}catch(SQLException sqle){throw sqle;}
	 	ruleList=db.getAllRules();
	 	adapter  = new RuleListAdapter(this, ruleList);
		listView.setAdapter(adapter);		
    }

/*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handling Menu item Clicks
		Intent intent = new Intent(this, BankActivity.class);
		switch (item.getItemId()) {
		case R.id.bank_add:
			// Calligng Bank Activity to add new Bank
			intent.putExtra("todo", "add");
		    startActivity(intent);
		    adapter.notifyDataSetChanged();
			return true;
		case R.id.bank_edit:
			intent.putExtra("todo", "edit");
		    startActivity(intent);
		    adapter.notifyDataSetChanged();
			return true;
		case R.id.bank_delete:
			DataBaseHelper db = new DataBaseHelper(this);
		 	try {	 
		 		db.openDataBase();
		 	} catch(SQLException sqle){throw sqle;}
			 db.deleteActiveBank();
			 db.setActiveAnyBank();
			 db.close();
			 bankList.remove(selected_row);
			 adapter.notifyDataSetChanged();
		    return true;
		}
		return false;
	}/**/
}
