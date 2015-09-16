package com.khizhny.ukrsibbanking;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupMenu.OnMenuItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class RuleListActivity extends AppCompatActivity implements OnMenuItemClickListener{
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
            	PopupMenu popupMenu = new PopupMenu(RuleListActivity.this, view);
        		popupMenu.setOnMenuItemClickListener(RuleListActivity.this);
        		popupMenu.inflate(R.menu.rule_list_popup_menu);
        		popupMenu.show();
            }
       }); 
	
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        ruleList=new ArrayList<Rule>();
    	DataBaseHelper db = new DataBaseHelper(this);
	 	db.openDataBase();
	 	ruleList=db.getAllRules();
	 	adapter  = new RuleListAdapter(this, ruleList);
		listView.setAdapter(adapter);		
    }

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// handles popup menu items click
		Rule r=adapter.getItem(selected_row);
		Intent intent;
		switch (item.getItemId()) {
		case R.id.item_new_rule:
			//Toast.makeText(this, "not implemented yet", Toast.LENGTH_SHORT).show();
			intent = new Intent(this, RuleActivity.class);
			intent.putExtra("sms_body", r.getSmsBody());
			intent.putExtra("todo", "add");
		    startActivity(intent);		    
			return true;
		case R.id.item_edit_rule:
			//Toast.makeText(this, "not implemented yet", Toast.LENGTH_SHORT).show();
			intent = new Intent(this, RuleActivity.class);
			intent.putExtra("rule_id", r.getId());
			intent.putExtra("todo", "edit");
		    startActivity(intent);		    
			return true;
		case R.id.item_delete_rule:
			DataBaseHelper db = new DataBaseHelper(this);
		 	db.openDataBase();
			db.deleteRule(r.getId());
			db.close();
			ruleList.remove(selected_row);
			adapter.notifyDataSetChanged();
		    return true;
		}
	return false;
	}


}
